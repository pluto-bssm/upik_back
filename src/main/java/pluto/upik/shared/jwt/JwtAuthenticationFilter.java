package pluto.upik.shared.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 요청에서 토큰을 추출합니다. (헤더 또는 쿼리 파라미터)
        String token = resolveToken(request);

        // 토큰이 유효한 경우
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // 토큰으로부터 인증 정보를 받아옵니다.
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            // SecurityContext에 인증 정보를 설정합니다.
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 다음 필터로 요청을 전달합니다.
        filterChain.doFilter(request, response);
    }

    /**
     * HttpServletRequest에서 토큰을 추출하는 메소드
     * 1. Authorization 헤더에서 Bearer 토큰을 찾습니다.
     * 2. 헤더에 토큰이 없다면, "accessToken"이라는 이름의 쿼리 파라미터에서 토큰을 찾습니다.
     */
    private String resolveToken(HttpServletRequest request) {
        // 1. 헤더에서 토큰 추출
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        // 2. 쿼리 파라미터에서 토큰 추출 (로그인 직후 리디렉션 시 사용)
        String queryToken = request.getParameter("accessToken");
        if (StringUtils.hasText(queryToken)) {
            return queryToken;
        }

        return null;
    }
}
