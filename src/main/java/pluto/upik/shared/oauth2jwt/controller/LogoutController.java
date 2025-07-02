package pluto.upik.shared.oauth2jwt.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pluto.upik.shared.oauth2jwt.service.RefreshTokenService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LogoutController {

    private final RefreshTokenService refreshTokenService;

    /**
     * 로그아웃 (리프레시 토큰 삭제)
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String refreshToken) {
        // "Bearer " 제거
        String token = refreshToken.replace("Bearer ", "");

        // 리프레시 토큰 삭제
        refreshTokenService.deleteRefreshToken(token);

        return ResponseEntity.ok("로그아웃 되었습니다.");
    }
}
