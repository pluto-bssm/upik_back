package pluto.upik.shared.oauth2jwt.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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

    // ✅ 인증 상태 확인 엔드포인트 추가
    @GetMapping("/status")
    public ResponseEntity<?> getAuthStatus() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("authenticated", false, "code", "UNAUTHORIZED"));
        }

        try {
            CustomOAuth2User user = (CustomOAuth2User) auth.getPrincipal();
            return ResponseEntity.ok(Map.of(
                    "authenticated", true,
                    "username", user.getUsername(),
                    "role", user.getAuthorities().iterator().next().getAuthority(),
                    "code", "AUTHENTICATED"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("authenticated", false, "code", "INVALID_TOKEN"));
        }
    }

    // AuthController.java - reissue 메서드 수정
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
            String newAccessToken = jwtUtil.createJwt("access", username, role, 900000L); // 15분

            // ✅ JavaScript에서 접근 가능한 쿠키 설정
            ResponseCookie cookie = ResponseCookie.from("Authorization", newAccessToken)
                    .path("/")
                    .maxAge(15 * 60) // 15분
                    .httpOnly(false) // ✅ JavaScript 접근 허용
                    .secure(false) // 개발환경
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
    public ResponseEntity<?> withdrawAccount(
            HttpServletRequest request,
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {

        try {
            // 1. 현재 사용자 확인
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "로그인이 필요합니다", "code", "UNAUTHORIZED"));
            }

            CustomOAuth2User user = (CustomOAuth2User) auth.getPrincipal();
            String username = user.getUsername();

            // ★★★ 이미 삭제된 사용자인지 확인 ★★★
            if (authService.isDeletedUser(username)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "이미 탈퇴된 계정입니다", "code", "ALREADY_DELETED"));
            }

            log.info("Account withdrawal requested by: {} (IP: {})",
                    username, getClientIP(request));

            // 2. 사용자 소프트 딜리트 실행
            boolean deleted = authService.softDeleteUser(username);

            if (!deleted) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "계정 탈퇴 처리에 실패했습니다", "code", "WITHDRAWAL_FAILED"));
            }

            // 3. 토큰 정리
            if (refreshToken != null && !refreshToken.isEmpty()) {
                authService.deleteRefreshTokenByToken(refreshToken);
            }

            // 4. 쿠키 삭제
            response.addHeader("Set-Cookie", createExpiredCookie("Authorization"));
            response.addHeader("Set-Cookie", createExpiredCookie("refreshToken"));

            // 5. 세션 무효화
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }

            // 6. SecurityContext 클리어
            SecurityContextHolder.clearContext();

            log.info("Account withdrawal completed successfully: {} -> deleted account", username);

            return ResponseEntity.ok(Map.of(
                    "message", "계정이 성공적으로 탈퇴되었습니다",
                    "code", "WITHDRAWAL_SUCCESS",
                    "note", "계정 정보는 보안을 위해 익명화되었습니다"
            ));

        } catch (Exception e) {
            log.error("Account withdrawal failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "계정 탈퇴 처리 중 오류가 발생했습니다", "code", "WITHDRAWAL_ERROR"));
        }
    }

    // ★★★ 클라이언트 IP 추출 헬퍼 메서드 ★★★
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    // ★★★ 쿠키 생성 헬퍼 메서드 ★★★
    private ResponseCookie createCookie(String key, String value) {
        return ResponseCookie.from(key, value)
                .path("/")
                .maxAge(24 * 60 * 60) // 1일
                .httpOnly(true)
                .secure(false) // 개발환경
                .build();
    }

    // ★★★ 만료된 쿠키 생성 헬퍼 메서드 ★★★
    private String createExpiredCookie(String cookieName) {
        return ResponseCookie.from(cookieName, "")
                .maxAge(0)
                .path("/")
                .httpOnly(true)
                .secure(false) // 개발환경
                .build()
                .toString();
    }
}