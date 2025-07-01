package pluto.upik.domain.oauth2.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import pluto.upik.domain.oauth2.data.dto.CustomOAuth2User;
import pluto.upik.shared.jwt.JwtTokenProvider;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        // 1. 인증 정보에서 CustomOAuth2User 와 Role 정보 가져오기
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        Collection<? extends GrantedAuthority> authorities = oAuth2User.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();
        String email = oAuth2User.getEmail();

        // 2. JWT Access Token 생성
        String accessToken = jwtTokenProvider.createAccessToken(email, role);

        // 3. 프론트엔드로 리디렉션. URL 파라미터에 토큰을 담아서 보낸다.
        String targetUrl = UriComponentsBuilder.fromUriString("/my")
                .queryParam("accessToken", accessToken)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
