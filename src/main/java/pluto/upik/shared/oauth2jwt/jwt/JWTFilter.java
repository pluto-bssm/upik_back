package pluto.upik.shared.oauth2jwt.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pluto.upik.shared.oauth2jwt.dto.CustomOAuth2User;
import pluto.upik.shared.oauth2jwt.dto.UserDTO;
import pluto.upik.shared.oauth2jwt.repository.RefreshTokenRepository;
import pluto.upik.shared.oauth2jwt.repository.UserRepository;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // ★★★ 1. 스킵할 경로 체크 ★★★
        String requestURI = request.getRequestURI();
        if (shouldSkipFilter(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        // ★★★ 2. Access Token 추출 ★★★
        String accessToken = extractTokenFromCookie(request);
        if (accessToken == null) {
            accessToken = extractTokenFromHeader(request);
        }

        // ★★★ 3. Access Token 처리 ★★★
        if (accessToken != null) {
            if (jwtUtil.isExpired(accessToken)) {
                // ★★★ 만료된 경우 Refresh Token으로 갱신 시도 ★★★
                if (handleExpiredAccessToken(request, response)) {
                    log.debug("Access token refreshed successfully for: {}", requestURI);
                } else {
                    log.debug("Failed to refresh access token for: {}", requestURI);
                }
            } else {
                // ★★★ 유효한 토큰으로 인증 설정 ★★★
                if (validateAndSetAuthentication(accessToken)) {
                    log.debug("Authentication set successfully for: {}", requestURI);
                }
            }
        } else {
            log.debug("No access token found for: {}", requestURI);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * ★★★ 만료된 Access Token 처리 ★★★
     */
    private boolean handleExpiredAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshTokenFromCookie(request);

        if (refreshToken == null) {
            log.debug("No refresh token found");
            return false;
        }

        // Refresh Token 검증
        if (jwtUtil.isExpired(refreshToken)) {
            log.debug("Refresh token is expired");
            return false;
        }

        if (!"refresh".equals(jwtUtil.getCategory(refreshToken))) {
            log.debug("Token is not a refresh token");
            return false;
        }

        if (!refreshTokenRepository.existsByToken(refreshToken)) {
            log.debug("Refresh token not found in database");
            return false;
        }

        try {
            // 새 Access Token 발급
            String username = jwtUtil.getUsername(refreshToken);
            String role = jwtUtil.getRole(refreshToken);
            String newAccessToken = jwtUtil.createAccessToken(username, role);

            // 새 토큰을 쿠키로 설정
            response.addHeader("Set-Cookie", createCookie("Authorization", newAccessToken));

            // 새 토큰으로 인증 설정
            return validateAndSetAuthentication(newAccessToken);

        } catch (Exception e) {
            log.warn("Failed to refresh access token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * ★★★ JWT 검증을 스킵할 경로들 ★★★
     */
    private boolean shouldSkipFilter(String requestURI) {
        return requestURI.startsWith("/oauth2/") ||
                requestURI.startsWith("/login/") ||
                requestURI.equals("/auth/reissue") ||
                requestURI.equals("/auth/logout") ||
                requestURI.equals("/auth/withdraw") ||  // ★★★ 추가 ★★★
                requestURI.startsWith("/static/") ||
                requestURI.startsWith("/css/") ||
                requestURI.startsWith("/js/") ||
                requestURI.startsWith("/images/") ||
                requestURI.equals("/favicon.ico") ||
                requestURI.equals("/error");
    }

    /**
     * ★★★ Authorization 헤더에서 Bearer 토큰 추출 ★★★
     */
    private String extractTokenFromHeader(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }

        return null;
    }

    /**
     * ★★★ 쿠키에서 Authorization 토큰 추출 ★★★
     */
    private String extractTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("Authorization".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    /**
     * ★★★ Refresh Token 쿠키 추출 ★★★
     */
    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    /**
     * ★★★ 쿠키 생성 헬퍼 메서드 ★★★
     */
    private String createCookie(String key, String value) {
        return ResponseCookie.from(key, value)
                .path("/")
                .maxAge(24 * 60 * 60) // 1일
                .httpOnly(true)
                .secure(false) // 개발환경
                .build()
                .toString();
    }

    /**
     * ★★★ 토큰 검증 및 인증 설정 ★★★
     */
    private boolean validateAndSetAuthentication(String accessToken) {
        try {
            // 토큰 카테고리 검증
            if (!"access".equals(jwtUtil.getCategory(accessToken))) {
                log.debug("Token is not an access token");
                return false;
            }

            // 토큰에서 사용자 정보 추출
            String username = jwtUtil.getUsername(accessToken);
            String role = jwtUtil.getRole(accessToken);

            if (username == null || role == null) {
                log.debug("Username or role is null in token");
                return false;
            }

            // ★★★ 삭제된 사용자인지 확인 ★★★
            if ("ROLE_DELETED".equals(role)) {
                log.warn("Deleted user attempted to access with token: {}", username);
                return false;
            }

            // DB에서 name 안전하게 조회
            String name = username; // 기본값
            try {
                Optional<String> nameOpt = userRepository.findNameByUsername(username);
                if (nameOpt.isPresent() && !nameOpt.get().trim().isEmpty()) {
                    name = nameOpt.get();
                }
            } catch (Exception e) {
                log.warn("DB에서 name 조회 실패: {}", e.getMessage());
            }

            // UserDTO 생성
            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(username);
            userDTO.setRole(role);
            userDTO.setName(name);

            CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO, userRepository);
            Authentication authToken = new UsernamePasswordAuthenticationToken(
                    customOAuth2User,
                    null,
                    customOAuth2User.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(authToken);

            log.debug("Authentication set for user: {} with role: {} name: {}", username, role, name);
            return true;

        } catch (Exception e) {
            log.warn("Token validation error: {}", e.getMessage());
            return false;
        }
    }
}