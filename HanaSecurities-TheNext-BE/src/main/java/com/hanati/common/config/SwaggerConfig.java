package com.hanati.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("하나증권 백엔드 API")
                .version("v1.0.0")
                .description("하나증권 백엔드 서버 API 명세서")
                .contact(new Contact()
                        .name("API Support")
                        .email("support@hanasec.com"));

        Server localServer = new Server()
                .url("http://localhost:8080")
                .description("로컬 개발 서버");

        Server prodServer = new Server()
                .url("https://api.hanasec.com")
                .description("운영 서버");

        SecurityScheme bearerAuth = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("한국투자증권 API 토큰을 사용한 인증");

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("bearerAuth");

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer, prodServer))
                .components(new Components().addSecuritySchemes("bearerAuth", bearerAuth))
                .security(List.of(securityRequirement));
    }
}