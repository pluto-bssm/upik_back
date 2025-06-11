package pluto.upik.shared.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import pluto.upik.shared.security.CustomUser;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        CustomUser user = (CustomUser) authentication.getPrincipal();

        String token = jwtProvider.generateToken(user.getName(), "ROLE_NOBSM");

        response.addHeader("Authorization", "Bearer " + token);

        // response.sendRedirect("http://yourfrontend.com?token=" + token);
    }
}
