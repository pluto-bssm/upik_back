package pluto.upik.shared.oauth2jwt.service;

import lombok.RequiredArgsConstructor;
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
            return null; // 또는 예외 처리
        }

        String username = oAuth2Response.getProvider() + "_" + oAuth2Response.getProviderId();

        // 1. username으로 DB에서 유저를 조회합니다. 결과는 Optional<User> 객체입니다.
        Optional<User> userOptional = userRepository.findByUsername(username);

        User user; // User 객체를 담을 변수 선언

        // 2. Optional의 isPresent() 메소드를 사용해 유저의 존재 여부를 확인합니다.
        if (userOptional.isPresent()) {
            // 유저가 이미 존재하면, Optional에서 User 객체를 가져와서 이름과 이메일 정보를 업데이트합니다.
            user = userOptional.get();
            user.setName(oAuth2Response.getName());
            user.setEmail(oAuth2Response.getEmail());
            // @Transactional에 의해 메소드 종료 시 자동으로 DB에 UPDATE 쿼리가 실행됩니다.
        } else {
            // 유저가 존재하지 않으면, 새로 생성합니다.
            User newUser = User.builder()
                    .username(username)
                    .email(oAuth2Response.getEmail())
                    .name(oAuth2Response.getName())
                    .role(getRole(oAuth2Response.getEmail()))
                    .build();
            // 새로 생성한 유저를 DB에 저장하고, user 변수에 할당합니다.
            user = userRepository.save(newUser);
        }

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(username);
        userDTO.setRole(user.getRole());
        userDTO.setName(user.getName());
        return new CustomOAuth2User(userDTO, userRepository);
    }
}