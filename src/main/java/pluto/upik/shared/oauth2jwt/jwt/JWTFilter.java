package pluto.upik.shared.oauth2jwt.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pluto.upik.shared.oauth2jwt.dto.CustomOAuth2User;
import pluto.upik.shared.oauth2jwt.dto.UserDTO;
import pluto.upik.shared.oauth2jwt.repository.UserRepository;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // ★★★ 1. OAuth2 및 정적 리소스 경로는 JWT 검증 스킵 ★★★
        String requestURI = request.getRequestURI();
        if (shouldSkipFilter(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        // ★★★ 2. Authorization 헤더에서 토큰 추출 (우선순위) ★★★
        String accessToken = extractTokenFromHeader(request);

        // ★★★ 3. 헤더에 없으면 쿠키에서 추출 ★★★
        if (accessToken == null) {
            accessToken = extractTokenFromCookie(request);
        }

        // ★★★ 4. 토큰이 없으면 다음 필터로 ★★★
        if (accessToken == null) {
            log.debug("No access token found in request to: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        // ★★★ 5. 토큰 검증 및 인증 설정 ★★★
        try {
            if (validateAndSetAuthentication(accessToken)) {
                log.debug("Authentication set successfully for request: {}", requestURI);
            } else {
                log.debug("Token validation failed for request: {}", requestURI);
            }
        } catch (Exception e) {
            log.warn("JWT processing error for request {}: {}", requestURI, e.getMessage());
            // 에러가 발생해도 다음 필터로 진행 (인증 실패로 처리됨)
        }

        filterChain.doFilter(request, response);
    }

    /**
     * ★★★ JWT 검증을 스킵할 경로들 ★★★
     */
    private boolean shouldSkipFilter(String requestURI) {
        return requestURI.startsWith("/oauth2/") ||
                requestURI.startsWith("/login/") ||
                requestURI.equals("/auth/reissue") ||
                requestURI.equals("/auth/logout") ||
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
            return authorizationHeader.substring(7); // "Bearer " 제거
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
                // ★★★ "Authorization" 쿠키에서 토큰 추출 ★★★
                if ("Authorization".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    /**
     * ★★★ 토큰 검증 및 인증 설정 ★★★
     */
    private boolean validateAndSetAuthentication(String accessToken) {
        try {
            // 토큰 만료 및 카테고리 검증
            if (jwtUtil.isExpired(accessToken)) {
                log.debug("Access token is expired");
                return false;
            }

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

            // ★★★ DB에서 name 안전하게 조회 ★★★
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
            userDTO.setName(name); // name 설정

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