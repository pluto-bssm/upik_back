package pluto.upik.shared.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import pluto.upik.domain.token.entity.RefreshToken;
import pluto.upik.domain.token.repository.RefreshTokenRepository;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        try {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            OAuth2User oauth2User = oauthToken.getPrincipal();

            String email = (String) oauth2User.getAttribute("email");

            if (email == null || email.isEmpty()) {
                response.sendRedirect("/login?error=no_email");
                return;
            }

            // BSM 학생 여부 확인 및 역할 결정
            boolean isBsmStudent = email.endsWith("@bssm.hs.kr");
            String userRole = isBsmStudent ? "BSM" : "NOBSM";

            // JWT 토큰 생성 (역할 정보 포함)
            String accessToken = jwtUtil.generateAccessToken(email, userRole);
            String refreshToken = jwtUtil.generateRefreshToken(email);

            refreshTokenRepository.save(
                    RefreshToken.builder()
                            .email(email)
                            .token(refreshToken)
                            .expiration(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000) // 7일
                            .build()
            );

            // 쿠키에 토큰 저장
            Cookie accessTokenCookie = new Cookie("access_token", accessToken);
            accessTokenCookie.setHttpOnly(true);
            accessTokenCookie.setSecure(false);
            accessTokenCookie.setPath("/");
            accessTokenCookie.setMaxAge(60 * 60); // 1시간

            Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(false);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge(60 * 60 * 24 * 7); // 7일

            response.addCookie(accessTokenCookie);
            response.addCookie(refreshTokenCookie);

            response.sendRedirect("/welcome");

        } catch (Exception e) {
            System.out.println("OAuth2 로그인 처리 중 오류: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("/login?error=processing_failed");
        }
    }
}