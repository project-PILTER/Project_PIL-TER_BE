package com.ll.projectLimC.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**")// 모든 엔드포인트에 적용
                // 프론트엔드 주소 && 실제 배포 주소
                .allowedOrigins("/http://localhost:3000", "/https://pliter.co.kr")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 HTTP 메서드
                .allowedHeaders("*") // 모든 헤더 허용
                .allowCredentials(true) // 프론트의 credentials: "include"에 대응
                .maxAge(3600); // 브라우저가 CORS 검사(Preflight) 결과를 캐싱할 시간 : 1시간
    }
}