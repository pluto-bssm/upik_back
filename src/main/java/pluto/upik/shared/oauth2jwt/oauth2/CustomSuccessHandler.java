package pluto.upik.shared.oauth2jwt.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pluto.upik.shared.oauth2jwt.dto.CustomOAuth2User;
import pluto.upik.shared.oauth2jwt.entity.User;
import pluto.upik.shared.oauth2jwt.jwt.JWTUtil;
import pluto.upik.shared.oauth2jwt.repository.UserRepository;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    @Value("oauth2.success.redirect-url")
    private String redirectUrl;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        log.info("=== OAuth2 로그인 성공 ===");

        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        String username = customOAuth2User.getUsername();
        String name = customOAuth2User.getName();
        String role = customOAuth2User.getRole();

        log.info("로그인 사용자: username={}, name={}, role={}", username, name, role);

        // ★★★ 안전한 사용자 저장 ★★★
        try {
            User existingUser = userRepository.findByUsername(username).orElse(null);

            if (existingUser == null) {
                User newUser = User.builder()
                        .username(username)
                        .name(name != null && !name.trim().isEmpty() ? name : username)
                        .role(role)
                        .email(getEmailFromAttributes(customOAuth2User.getAttributes()))
                        .build();

                userRepository.save(newUser);
                log.info("새 사용자 저장 완료: {}", username);
            } else {
                log.info("기존 사용자 로그인: {}", username);
            }
        } catch (Exception e) {
            log.error("사용자 정보 처리 중 오류: {}", e.getMessage());
        }

        // ★★★ JWT Access Token 생성 ★★★
        String accessToken = jwtUtil.createAccessToken(username, role);

        // 쿠키 설정 및 리다이렉트
        response.addCookie(createCookie("Authorization", accessToken));
        response.sendRedirect(redirectUrl);

        log.info("OAuth2 로그인 완료: {}", username);
    }

    /**
     * ★★★ 이메일 안전하게 추출 ★★★
     */
    private String getEmailFromAttributes(Map<String, Object> attributes) {
        try {
            Object email = attributes.get("email");
            return email != null ? email.toString() : "";
        } catch (Exception e) {
            log.warn("이메일 추출 실패: {}", e.getMessage());
            return "";
        }
    }

    /**
     * ★★★ 쿠키 생성 ★★★
     */
    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60 * 60 * 24); // 24시간
        cookie.setSecure(false); // 개발환경
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }
}