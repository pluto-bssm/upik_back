package pluto.upik.shared.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import pluto.upik.domain.oauth2.service.CustomOAuth2UserService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable);

        http
                .formLogin(AbstractHttpConfigurer::disable);

        http
                .httpBasic(AbstractHttpConfigurer::disable);

        http
                .oauth2Login((oauth2) -> oauth2
                        .loginPage("/login")
                        // ✅ 이 부분을 추가하여 로그인 성공 시 /my 경로로 리다이렉트합니다.
                        .defaultSuccessUrl("/my", true)
                        .userInfoEndpoint((userInfoEndpointConfig) ->
                                userInfoEndpointConfig.userService(customOAuth2UserService)));


        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/", "/oauth2/**", "/login/**").permitAll()
                        // /my 경로도 인증된 사용자가 접근할 수 있도록 규칙이 필요합니다.
                        .requestMatchers("/my").hasAnyRole("NOBSM", "BSM", "ADMIN")
                        .anyRequest().authenticated());

        return http.build();
    }
}