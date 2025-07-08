package pluto.upik.shared.oauth2jwt.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pluto.upik.shared.oauth2jwt.dto.CustomOAuth2User;
import pluto.upik.shared.oauth2jwt.entity.RefreshToken;
import pluto.upik.shared.oauth2jwt.entity.User;
import pluto.upik.shared.oauth2jwt.jwt.JWTUtil;
import pluto.upik.shared.oauth2jwt.repository.RefreshTokenRepository;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-token-expiration-time}")
    private long refreshTokenExpirationTime;

    @Value("${oauth2.success.redirect-url}")
    private String redirectUrl;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        // Optional<User>에서 User 객체를 안전하게 추출
        Optional<User> userOptional = customOAuth2User.getUser();

        if (userOptional.isEmpty()) {
            // 사용자 정보가 없는 경우 에러 처리
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "User information not found");
            return;
        }

        User user = userOptional.get();
        String userId = user.getId().toString();
        String role = user.getRole();

        String accessToken = jwtUtil.createJwt("access", userId, role);
        String refreshTokenValue = jwtUtil.createJwt("refresh", userId, role);

        saveOrUpdateRefreshToken(user, refreshTokenValue);

        response.addHeader("Set-Cookie", createCookie("accessToken", accessToken).toString());
        response.addHeader("Set-Cookie", createCookie("refreshToken", refreshTokenValue).toString());
        response.sendRedirect(redirectUrl);
    }

    private void saveOrUpdateRefreshToken(User user, String tokenValue) {
        Date expiryDate = new Date(System.currentTimeMillis() + refreshTokenExpirationTime);

        refreshTokenRepository.findByUser(user)
                .ifPresentOrElse(
                        // 기존 토큰이 있으면 값과 만료 시간 업데이트
                        existingToken -> {
                            existingToken.updateToken(tokenValue, expiryDate);
                            // @Transactional로 인해 자동으로 업데이트됨 (save 호출 불필요)
                        },
                        // 기존 토큰이 없으면 새로 생성
                        () -> {
                            RefreshToken newRefreshToken = RefreshToken.builder()
                                    .user(user)
                                    .token(tokenValue)
                                    .role(user.getRole())
                                    .expiryDate(expiryDate)
                                    .build();
                            refreshTokenRepository.save(newRefreshToken);
                        }
                );
    }

    private ResponseCookie createCookie(String key, String value) {
        return ResponseCookie.from(key, value)
                .maxAge(60 * 60 * 24 * 7) // 7일
                .path("/")
                .httpOnly(true)
                // .secure(true) // HTTPS 배포 시 주석 해제
                // .sameSite("Lax") // CSRF 공격 방지를 위해 추가 고려
                .build();
    }
}