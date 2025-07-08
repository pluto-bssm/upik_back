package pluto.upik.shared.oauth2jwt.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie; // ResponseCookie 임포트
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pluto.upik.shared.oauth2jwt.jwt.JWTUtil;
import pluto.upik.shared.oauth2jwt.repository.RefreshTokenRepository;
import pluto.upik.shared.oauth2jwt.service.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthService authService;

    // reissue 엔드포인트: 쿠키 이름을 'refreshToken'으로 통일
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@CookieValue("refreshToken") String refreshToken, HttpServletResponse response) {
        // 1. Refresh Token 유효성 검증
        try {
            if (jwtUtil.isExpired(refreshToken) || !"refresh".equals(jwtUtil.getCategory(refreshToken))) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired refresh token.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token format.");
        }

        // 2. DB에 저장된 토큰인지 확인
        if (!refreshTokenRepository.existsByToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token not found in database.");
        }

        // 3. 새로운 Access Token 생성
        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);
        String newAccessToken = jwtUtil.createJwt("access", username, role);

        // 4. 새로운 Access Token을 쿠키에 담아 응답 (개선된 createCookie 사용)
        response.addHeader("Set-Cookie", createCookie("Authorization", newAccessToken).toString());

        return ResponseEntity.ok("Access token reissued successfully.");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            HttpServletRequest request, // HttpServletRequest 주입
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {

        // 1. DB에서 Refresh Token 삭제
        if (refreshToken != null && !refreshToken.isEmpty()) {
            authService.deleteRefreshTokenByToken(refreshToken);
        }

        // 2. 클라이언트의 모든 관련 쿠키들을 만료시킴
        response.addHeader("Set-Cookie", createExpiredCookie("access"));
        response.addHeader("Set-Cookie", createExpiredCookie("refreshToken"));

        // 3. ★★★ 서버 세션 무효화 ★★★
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        return ResponseEntity.ok("로그아웃 되었습니다.");
    }

    private ResponseCookie createCookie(String key, String value) {
        return ResponseCookie.from(key, value)
                .path("/")
                .maxAge(24 * 60 * 60) // 1일
                .httpOnly(true)
                // .secure(true) // HTTPS 환경에서만 사용
                // .sameSite("None") // CSRF 방지
                .build();
    }

    // ★★★ 명확한 쿠키 만료 헬퍼 메소드 ★★★
    private String createExpiredCookie(String cookieName) {
        return ResponseCookie.from(cookieName, "")
                .maxAge(0)
                .path("/")
                .httpOnly(true)
                // .secure(true)
                // .sameSite("None")
                .build()
                .toString();
    }
}