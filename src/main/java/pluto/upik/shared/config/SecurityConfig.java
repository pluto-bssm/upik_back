package pluto.upik.shared.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import pluto.upik.shared.oauth2jwt.service.CustomOAuth2UserService;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final JWTUtil jwtUtil;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // ★★★ 1. CORS 설정 추가 (가장 먼저!) ★★★
        // 프론트엔드(http://localhost:5173)에서 오는 요청을 허용하기 위한 설정입니다.
        http.cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration configuration = new CorsConfiguration();
                // "http://localhost:5173" 주소의 요청을 허용합니다.
                configuration.setAllowedOrigins(Collections.singletonList("http://localhost:5173"));
                // 모든 HTTP 메서드(GET, POST, 등)를 허용합니다.
                configuration.setAllowedMethods(Collections.singletonList("*"));
                // 쿠키를 포함한 요청을 허용합니다 (매우 중요!).
                configuration.setAllowCredentials(true);
                // 모든 헤더를 허용합니다.
                configuration.setAllowedHeaders(Collections.singletonList("*"));
                configuration.setMaxAge(3600L); // Pre-flight 요청 캐시 시간
                return configuration;
            }
        }));


        // 기존 설정들
        http.csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 기본 로그아웃 핸들러 비활성화
        http.logout(logout -> logout.disable());


        // OAuth2 로그인 설정
        http.oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig.userService(customOAuth2UserService))
                .successHandler(customSuccessHandler));

        // 경로별 접근 권한 설정
        http.authorizeHttpRequests(auth -> auth
                // ★★★ 2. "http://localhost:5173"를 제거하고 올바른 경로만 남깁니다. ★★★
                .requestMatchers("/", "/oauth2/**", "/login/**", "/auth/reissue", "/auth/logout").permitAll()
                .anyRequest().authenticated());

        // JWT 필터 추가
        http.addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}