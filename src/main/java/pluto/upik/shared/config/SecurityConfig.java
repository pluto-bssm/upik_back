package pluto.upik.shared.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import pluto.upik.shared.security.KakaoOAuth2UserService;

/**
 * 보안 설정 클래스
 * 애플리케이션의 보안 설정을 정의합니다.
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final KakaoOAuth2UserService kakaoOAuth2UserService;

    /**
     * 보안 필터 체인 설정
     * 
     * @param http HttpSecurity 객체
     * @return 구성된 SecurityFilterChain
     * @throws Exception 보안 설정 중 발생할 수 있는 예외
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .csrf(AbstractHttpConfigurer::disable) // API 서버이므로 CSRF 비활성화
                .cors(AbstractHttpConfigurer::disable) // CORS 비활성화
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // 모든 요청 허용
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(kakaoOAuth2UserService)
                        )
                );

        return http.build();
    }
}