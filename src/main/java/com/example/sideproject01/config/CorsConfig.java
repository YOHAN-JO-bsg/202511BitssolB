// src/main/java/com/example/sideproject01/config/CorsConfig.java
package com.example.sideproject01.config;

import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")                  // 백엔드 API 경로
                .allowedOrigins("http://localhost:5173")// 프론트 개발 오리진(Vite)
                .allowedMethods("GET","POST","PUT","DELETE","PATCH","OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Content-Type")
                .allowCredentials(false)                // 쿠키/세션 필요 없으면 false 권장
                .maxAge(3600);                          // preflight 캐시(초)
    }
}
