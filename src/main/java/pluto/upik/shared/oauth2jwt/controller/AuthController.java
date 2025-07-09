package pluto.upik.shared.oauth2jwt.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pluto.upik.shared.oauth2jwt.dto.CustomOAuth2User;
import pluto.upik.shared.oauth2jwt.jwt.JWTUtil;
import pluto.upik.shared.oauth2jwt.repository.RefreshTokenRepository;
import pluto.upik.shared.oauth2jwt.service.AuthService;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthService authService;

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {

        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Refresh token is required", "code", "MISSING_REFRESH_TOKEN"));
        }

        try {
            if (jwtUtil.isExpired(refreshToken) || !"refresh".equals(jwtUtil.getCategory(refreshToken))) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired refresh token", "code", "INVALID_REFRESH_TOKEN"));
            }

            if (!refreshTokenRepository.existsByToken(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Refresh token not found", "code", "TOKEN_NOT_FOUND"));
            }

            String username = jwtUtil.getUsername(refreshToken);
            String role = jwtUtil.getRole(refreshToken);
            String newAccessToken = jwtUtil.createJwt("access", username, role, 900000L);

            ResponseCookie cookie = ResponseCookie.from("Authorization", newAccessToken)
                    .path("/")
                    .maxAge(15 * 60)
                    .httpOnly(false)
                    .secure(false)
                    .sameSite("Lax")
                    .build();

            response.addHeader("Set-Cookie", cookie.toString());

            return ResponseEntity.ok(Map.of(
                    "message", "Access token reissued successfully",
                    "code", "REISSUE_SUCCESS"
            ));

        } catch (Exception e) {
            log.error("Token reissue failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token reissue failed", "code", "REISSUE_FAILED"));
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdrawAccount(HttpServletRequest request, HttpServletResponse response) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "로그인이 필요합니다", "code", "UNAUTHORIZED"));
            }

            CustomOAuth2User user = (CustomOAuth2User) auth.getPrincipal();
            String username = user.getUsername();

            if (authService.isDeletedUser(username)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "이미 탈퇴된 계정입니다", "code", "ALREADY_DELETED"));
            }

            boolean deleted = authService.softDeleteUser(username);
            if (!deleted) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "계정 탈퇴 처리에 실패했습니다", "code", "WITHDRAWAL_FAILED"));
            }

            // 쿠키 삭제
            response.addHeader("Set-Cookie", createExpiredCookie("Authorization"));
            response.addHeader("Set-Cookie", createExpiredCookie("refreshToken"));

            SecurityContextHolder.clearContext();

            return ResponseEntity.ok(Map.of(
                    "message", "계정이 성공적으로 탈퇴되었습니다",
                    "code", "WITHDRAWAL_SUCCESS"
            ));

        } catch (Exception e) {
            log.error("Account withdrawal failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "계정 탈퇴 처리 중 오류가 발생했습니다", "code", "WITHDRAWAL_ERROR"));
        }
    }

    private String createExpiredCookie(String cookieName) {
        return ResponseCookie.from(cookieName, "")
                .maxAge(0)
                .path("/")
                .httpOnly(true)
                .secure(false)
                .build()
                .toString();
    }
}