package com.ll.projectLimC.global.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;

import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Pilter API",
                description = "Pilter API 명세서 입니다."
        ),
        servers = {
                @Server(url = "https://pilter.co.kr/api", description = "배포 서버"),
                @Server(url = "http://localhost:8080/api", description = "로컬 서버")
        }
)
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        SecurityScheme securityScheme = getSecurityScheme();
        SecurityRequirement securityRequirement = getSecurityRequirement();

        return new OpenAPI()
                .components(new Components().addSecuritySchemes("bearerAuth", securityScheme))
                .security( List.of(securityRequirement));
    }

    private SecurityScheme getSecurityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");
    }

    private SecurityRequirement getSecurityRequirement() {
        return new SecurityRequirement().addList("bearerAuth");
    }
}
//        // 1. 로컬 개발용 서버 주소 정의
//        Server localServer = new Server();
//        localServer.setUrl("");
//        localServer.setDescription("로컬 테스트 서버");
//
//        // 2. 운영 서버 주소 정의
//        Server prodServer = new Server();
//        prodServer.setUrl("https://pilter.co.kr");
//        prodServer.setDescription("운영 실서버");