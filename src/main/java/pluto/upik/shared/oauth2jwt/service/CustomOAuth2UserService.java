package pluto.upik.shared.oauth2jwt.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pluto.upik.shared.oauth2jwt.dto.CustomOAuth2User;
import pluto.upik.shared.oauth2jwt.dto.GoogleResponse;
import pluto.upik.shared.oauth2jwt.dto.OAuth2Response;
import pluto.upik.shared.oauth2jwt.dto.UserDTO;
import pluto.upik.shared.oauth2jwt.entity.User;
import pluto.upik.shared.oauth2jwt.repository.UserRepository;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public String getRole(String email) {
        if (email != null && email.endsWith("@bssm.hs.kr")) {
            return "ROLE_BSM";
        }
        return "ROLE_NOBSM";
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2Response oAuth2Response;
        if (registrationId.equals("google")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        } else {
            throw new OAuth2AuthenticationException("지원하지 않는 OAuth2 제공자입니다: " + registrationId);
        }

        String username = oAuth2Response.getProvider() + "_" + oAuth2Response.getProviderId();

        // ★★★ 1. 삭제된 사용자 포함 모든 사용자 조회 ★★★
        Optional<User> existingUser = userRepository.findByUsernameIncludingDeleted(username);

        User user;

        if (existingUser.isPresent()) {
            // ★★★ 2. 기존 사용자 처리 ★★★
            user = handleExistingUser(existingUser.get(), oAuth2Response);
            log.info("기존 사용자 처리 완료 - username: {}, role: {}", username, user.getRole());
        } else {
            // ★★★ 3. 완전히 새로운 사용자 생성 ★★★
            user = createNewUser(username, oAuth2Response);
            log.info("신규 사용자 생성 - username: {}, role: {}", username, user.getRole());
        }

        // UserDTO 생성 및 반환
        UserDTO userDTO = UserDTO.builder()
                .username(username)
                .role(user.getRole())
                .name(user.getName())
                .build();

        return new CustomOAuth2User(userDTO, userRepository);
    }

    /**
     * ★★★ 기존 사용자 처리 로직 ★★★
     */
    private User handleExistingUser(User existingUser, OAuth2Response oAuth2Response) {

        if ("ROLE_DELETED".equals(existingUser.getRole())) {
            // ★★★ 탈퇴한 사용자 복구 ★★★
            return reactivateDeletedUser(existingUser, oAuth2Response);
        } else {
            // ★★★ 활성 사용자 정보 업데이트 ★★★
            existingUser.setName(oAuth2Response.getName());
            existingUser.setEmail(oAuth2Response.getEmail());

            log.info("활성 사용자 정보 업데이트 - username: {}", existingUser.getUsername());
            return existingUser;
        }
    }

    /**
     * ★★★ 탈퇴한 사용자 복구 ★★★
     */
    private User reactivateDeletedUser(User deletedUser, OAuth2Response oAuth2Response) {
        log.info("탈퇴한 계정 복구 시작 - ID: {}, username: {}",
                deletedUser.getId(), deletedUser.getUsername());

        // 1. 역할 복구 (이메일 기반으로 재설정)
        String newRole = getRole(oAuth2Response.getEmail());
        deletedUser.setRole(newRole);

        // 2. 사용자 정보 업데이트
        deletedUser.setName(oAuth2Response.getName());
        deletedUser.setEmail(oAuth2Response.getEmail());

        log.info("계정 복구 완료 - ID: {}, username: {}, 새로운 역할: {}",
                deletedUser.getId(), deletedUser.getUsername(), newRole);

        return deletedUser; // @Transactional에 의해 자동 저장
    }

    /**
     * ★★★ 신규 사용자 생성 ★★★
     */
    private User createNewUser(String username, OAuth2Response oAuth2Response) {
        User newUser = User.builder()
                .username(username)
                .email(oAuth2Response.getEmail())
                .name(oAuth2Response.getName())
                .role(getRole(oAuth2Response.getEmail()))
                .build();

        return userRepository.save(newUser);
    }
}