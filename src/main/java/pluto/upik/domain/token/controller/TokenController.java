package pluto.upik.domain.token.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pluto.upik.domain.token.repository.RefreshTokenRepository;
import pluto.upik.shared.security.JwtUtil;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class TokenController {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @PostMapping("/refresh")
    public String refreshToken(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = extractTokenFromCookie(request, "refresh_token");

        if (refreshToken == null || !jwtUtil.validateToken(refreshToken)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "Invalid refresh token";
        }

        String email = jwtUtil.getEmailFromToken(refreshToken);
        var storedToken = refreshTokenRepository.findById(email).orElse(null);

        if (storedToken == null || !storedToken.getToken().equals(refreshToken)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "Refresh token mismatch";
        }

        // 재발급
        String newAccessToken = jwtUtil.generateAccessToken(email, jwtUtil.getRoleFromToken(refreshToken));
        String newRefreshToken = jwtUtil.generateRefreshToken(email);

        // 저장
        storedToken.setToken(newRefreshToken);
        storedToken.setExpiration(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000);
        refreshTokenRepository.save(storedToken);

        // 쿠키 갱신
        Cookie accessCookie = new Cookie("access_token", newAccessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(60 * 60); // 1시간

        Cookie refreshCookie = new Cookie("refresh_token", newRefreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(60 * 60 * 24 * 7); // 7일

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        return "Token refreshed";
    }

    private String extractTokenFromCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(name)) return cookie.getValue();
        }
        return null;
    }
}