package pluto.upik.shared.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger 설정 클래스
 * API 문서화를 위한 Swagger 설정을 정의합니다.
 */
@Configuration
public class SwaggerConfig {

    /**
     * OpenAPI 설정을 정의합니다.
     * 
     * @return OpenAPI 설정 객체
     */
    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("Upik API")
                .version("v1.0")
                .description("Upik 애플리케이션의 API 문서");

        // JWT 인증 스키마 설정
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");

        return new OpenAPI()
                .info(info)
                .components(new Components().addSecuritySchemes("bearerAuth", securityScheme))
                .addSecurityItem(securityRequirement);
    }
}