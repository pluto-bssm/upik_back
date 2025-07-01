package pluto.upik.shared.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy; // import 추가
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // import 추가
import pluto.upik.domain.oauth2.handler.OAuth2AuthenticationSuccessHandler; // import 추가
import pluto.upik.domain.oauth2.service.CustomOAuth2UserService;
import pluto.upik.shared.jwt.JwtAuthenticationFilter;
import pluto.upik.shared.jwt.JwtTokenProvider;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    // 이전에 만든 SuccessHandler와 JwtTokenProvider를 주입받습니다.
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        // ★ 세션을 사용하지 않도록 STATELESS로 설정
        http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // ★ OAuth2 로그인 설정 수정
        http
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        // ★ defaultSuccessUrl 대신 커스텀 핸들러를 사용하여 JWT 발급
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                );


        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/", "/oauth2/**", "/login/**").permitAll()
                        .requestMatchers("/my").hasAnyRole("NOBSM", "BSM", "ADMIN")
                        .anyRequest().authenticated());

        // ★ JWT 인증 필터를 UsernamePasswordAuthenticationFilter 앞에 추가
        // 이렇게 해야 모든 API 요청에 대해 JWT 토큰을 먼저 검사합니다.
        http
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }
}