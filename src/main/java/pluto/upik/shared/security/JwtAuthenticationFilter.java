package pluto.upik.shared.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pluto.upik.domain.oauth2.service.CustomUserDetailsService;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        String token = extractTokenFromRequest(request);

        if (token != null) {
            if (jwtUtil.validateToken(token)) {
                String email = jwtUtil.getEmailFromToken(token);

                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    try {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails, null, userDetails.getAuthorities());

                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);

                    } catch (UsernameNotFoundException e) {
                        logger.warn("User not found: " + email);
                    } catch (Exception e) {
                        System.out.println("사용자 로드 중 예상치 못한 오류 발생");
                        System.out.println("오류 타입: " + e.getClass().getSimpleName());
                        System.out.println("오류 메시지: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else if (email == null) {
                    System.out.println("토큰에서 이메일을 추출할 수 없음");
                } else {
                    System.out.println("이미 인증된 사용자 (인증 컨텍스트 존재)");
                    System.out.println("현재 인증된 사용자: " + SecurityContextHolder.getContext().getAuthentication().getName());
                }
            } else {
                System.out.println("토큰 유효성 검증 실패!");
                System.out.println("유효하지 않은 토큰으로 인해 인증 불가");
            }
        } else {
            System.out.println("JWT 토큰이 없음");
            System.out.println("공개 리소스 접근 또는 미인증 요청");
        }

        filterChain.doFilter(request, response);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {

        // 1. Authorization 헤더에서 토큰 추출 시도
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            System.out.println("성공적으로 추출"+bearerToken.substring(7));
            return bearerToken.substring(7);
        } else if (bearerToken != null) {
            System.out.println("Authorization 헤더는 있지만 Bearer 형식이 아님: " + bearerToken);
        } else {
            System.out.println("Authorization 헤더 없음");
        }

        // 2. 쿠키에서 토큰 추출 시도
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("access_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
            System.out.println("access_token 쿠키를 찾을 수 없음");
        } else {
            System.out.println("쿠키가 없음");
        }

        // 3. URL 파라미터에서 토큰 추출 시도
        String tokenParam = request.getParameter("token");
        if (tokenParam != null && !tokenParam.isEmpty()) {
            return tokenParam;
        } else {
            System.out.println("URL 파라미터에 토큰 없음");
        }

        System.out.println("모든 위치에서 토큰을 찾을 수 없음");
        return null;
    }
}