package pluto.upik.shared.oauth2jwt.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pluto.upik.shared.oauth2jwt.jwt.JWTUtil; // 변경된 부분: JWTUtil 사용
import pluto.upik.shared.oauth2jwt.repository.RefreshTokenRepository;
import pluto.upik.shared.oauth2jwt.service.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthService authService;

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@CookieValue("refresh") String refreshToken, HttpServletResponse response) {
        // 1. Refresh Token 유효성 검증
        try {
            if (jwtUtil.isExpired(refreshToken) || !"refresh".equals(jwtUtil.getCategory(refreshToken))) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired refresh token.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token format.");
        }

        // 2. DB에 저장된 토큰인지 확인
        if (!refreshTokenRepository.existsByToken(refreshToken)) { // 변경된 부분: existsByToken 사용
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token not found in database.");
        }

        // 3. 새로운 Access Token 생성
        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);
        String newAccessToken = jwtUtil.createJwt("access", username, role);

        // 4. 새로운 Access Token을 쿠키에 담아 응답
        response.addCookie(createCookie("access", newAccessToken));

        return ResponseEntity.ok("Access token reissued successfully.");
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60 * 60 * 24); // 쿠키 유효기간 1일
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        return cookie;
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @CookieValue(value = "refresh", required = false) String refreshToken,
            HttpServletResponse response) {

        // DB에서 Refresh Token 삭제 로직을 서비스에 위임
        if (refreshToken != null && !refreshToken.isEmpty()) {
            System.out.println("hello world");
            authService.deleteRefreshTokenByToken(refreshToken);
        }

        System.out.println(refreshToken);

        // 클라이언트의 토큰 쿠키들을 만료시킴
        expireCookie(response, "access");
        expireCookie(response, "refresh");

        return ResponseEntity.ok("Logout successful.");
    }

    private void expireCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}