package pluto.upik.shared.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import pluto.upik.shared.oauth2jwt.jwt.JWTFilter;
import pluto.upik.shared.oauth2jwt.jwt.JWTUtil;
import pluto.upik.shared.oauth2jwt.oauth2.CustomSuccessHandler;
import pluto.upik.shared.oauth2jwt.repository.RefreshTokenRepository;
import pluto.upik.shared.oauth2jwt.repository.UserRepository;
import pluto.upik.shared.oauth2jwt.service.AuthService;
import pluto.upik.shared.oauth2jwt.service.CustomOAuth2UserService;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final JWTUtil jwtUtil;
    private final AuthService authService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, UserRepository userRepository) throws Exception {

        // CORS 설정
        http.cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(Arrays.asList(
                        "http://localhost:8080",
                        "http://localhost:5173"
                ));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
                configuration.setAllowCredentials(true);
                configuration.setAllowedHeaders(Collections.singletonList("*"));
                configuration.setExposedHeaders(Arrays.asList("Authorization", "Set-Cookie"));
                configuration.setMaxAge(3600L);
                return configuration;
            }
        }));

        // 기본 설정
        http.csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 로그아웃 설정
        http.logout(logout -> logout
                .logoutUrl("/auth/logout")
                .addLogoutHandler((request, response, authentication) -> {
                    Cookie[] cookies = request.getCookies();
                    if (cookies != null) {
                        for (Cookie cookie : cookies) {
                            if ("refreshToken".equals(cookie.getName())) {
                                authService.deleteRefreshTokenByToken(cookie.getValue());
                                break;
                            }
                        }
                    }
                })
                .logoutSuccessHandler((request, response, authentication) -> {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"message\":\"로그아웃 성공\"}");
                })
                .deleteCookies("refreshToken", "Authorization")
        );

        // OAuth2 로그인
        http.oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig.userService(customOAuth2UserService))
                .successHandler(customSuccessHandler));

        // 접근 권한 설정 - 단순화
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/oauth2/**", "/login/**", "/auth/reissue").permitAll()
                .requestMatchers("/api/my").permitAll()
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
        );

        // 예외 처리
        http.exceptionHandling(exceptions -> exceptions
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"error\":\"접근 권한이 없습니다\",\"code\":\"ACCESS_DENIED\"}");
                })
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"error\":\"로그인이 필요합니다\",\"code\":\"UNAUTHORIZED\"}");
                })
        );

        // JWT 필터
        http.addFilterBefore(new JWTFilter(jwtUtil, userRepository, refreshTokenRepository), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}