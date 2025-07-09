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
import pluto.upik.shared.oauth2jwt.entity.RefreshToken;
import pluto.upik.shared.oauth2jwt.entity.User;
import pluto.upik.shared.oauth2jwt.jwt.JWTUtil;
import pluto.upik.shared.oauth2jwt.repository.RefreshTokenRepository;
import pluto.upik.shared.oauth2jwt.repository.UserRepository;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${oauth2.success.redirect-url}")
    private String redirectUrl;

    @Value("${jwt.refresh-token-expiration-time}")
    private long refreshTokenExpirationTime;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        try {
            CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
            String username = customOAuth2User.getUsername();
            String role = customOAuth2User.getRole();

            log.info("OAuth2 login success: username={}, role={}", username, role);

            // ★★★ 활성 사용자만 조회 (ROLE_DELETED 제외) ★★★
            Optional<User> activeUser = userRepository.findByUsername(username);

            if (activeUser.isEmpty()) {
                log.error("Active user not found after OAuth2 authentication: {}", username);
                response.sendRedirect("/login?error=user_not_found");
                return;
            }

            User user = activeUser.get();

            // ★★★ Access Token과 Refresh Token 모두 생성 ★★★
            String accessToken = jwtUtil.createAccessToken(username, role);
            String refreshToken = jwtUtil.createRefreshToken(username, role);

            log.debug("Tokens created successfully for user: {}", username);

            // ★★★ DB에 Refresh Token 저장 ★★★
            // 기존 Refresh Token 삭제
            refreshTokenRepository.findByUser(user).ifPresent(existingToken -> {
                log.debug("Deleting existing refresh token for user: {}", username);
                refreshTokenRepository.delete(existingToken);
            });

            // 새 Refresh Token 저장
            RefreshToken refreshTokenEntity = RefreshToken.builder()
                    .user(user)
                    .token(refreshToken)
                    .role(role)
                    .expiryDate(new Date(System.currentTimeMillis() + refreshTokenExpirationTime))
                    .build();
            refreshTokenRepository.save(refreshTokenEntity);

            log.debug("New refresh token saved for user: {}", username);

            // ★★★ 두 토큰 모두 쿠키에 저장 ★★★
            response.addCookie(createCookie("Authorization", accessToken));
            response.addCookie(createRefreshCookie("refreshToken", refreshToken));

            log.info("OAuth2 login completed successfully: {}", username);

            response.sendRedirect(redirectUrl);

        } catch (Exception e) {
            log.error("OAuth2 login success handler failed: {}", e.getMessage(), e);
            response.sendRedirect("/login?error=handler_error");
        }
    }

    private Cookie createRefreshCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60 * 60 * 24 * 7); // 7일
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setHttpOnly(false);
        return cookie;
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60 * 60 * 24); // 24시간
        cookie.setSecure(false); // 개발환경
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }
}