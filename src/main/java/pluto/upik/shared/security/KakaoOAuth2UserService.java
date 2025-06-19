package pluto.upik.shared.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pluto.upik.domain.user.data.model.User;
import pluto.upik.domain.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 카카오 OAuth2 사용자 서비스
 * 카카오 OAuth2 인증을 처리하고 사용자 정보를 관리합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    /**
     * OAuth2 사용자 정보를 로드하고 사용자 엔티티를 생성하거나 업데이트합니다.
     *
     * @param oAuth2UserRequest OAuth2 사용자 요청
     * @return 커스텀 OAuth2 사용자
     */
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String kakaoId = String.valueOf(attributes.get("id"));
        String nickname = extractNickname(attributes);

        // 사용자 권한 설정
        Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        // 사용자 엔티티 생성 또는 업데이트
        User user = userRepository.findByUsername(kakaoId)
                .orElseGet(() -> createUser(kakaoId, nickname));

        log.info("카카오 OAuth2 사용자 로그인: {}", user.getUsername());

        // CustomUser 생성
        CustomUser customUser = new CustomUser(attributes, authorities);

        return customUser;
    }

    /**
     * 카카오 계정 정보에서 닉네임을 추출합니다.
     *
     * @param attributes 카카오 계정 속성
     * @return 닉네임
     */
    private String extractNickname(Map<String, Object> attributes) {
        Object kakaoAccountObj = attributes.get("kakao_account");
        if (kakaoAccountObj instanceof Map<?, ?> kakaoAccount) {
            Object profileObj = kakaoAccount.get("profile");
            if (profileObj instanceof Map<?, ?> profile) {
                Object nicknameObj = profile.get("nickname");
                return nicknameObj != null ? nicknameObj.toString() : "Unknown";
            }
        }
        return "Unknown";
    }

    /**
     * 새로운 사용자 엔티티를 생성합니다.
     *
     * @param kakaoId 카카오 ID
     * @param nickname 닉네임
     * @return 생성된 사용자 엔티티
     */
    private User createUser(String kakaoId, String nickname) {
        User user = User.builder()
                .id(UUID.randomUUID())
                .username(kakaoId)
                .name(nickname)
                .role(User.Role.USER)
                .build();

        return userRepository.save(user);
    }
}
