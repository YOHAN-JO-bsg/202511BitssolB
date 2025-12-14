// src/main/java/com/example/sideproject01/config/HttpClientConfig.java
package com.example.sideproject01.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

/**
 * RestTemplate 설정 (호환성 높은 방식)
 * - 타임아웃을 SimpleClientHttpRequestFactory에 직접 지정
 * - Spring 버전/의존성과 무관하게 안정적으로 동작
 */
@Configuration
public class HttpClientConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        // 1) 기본 JDK HttpURLConnection 기반 팩토리
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

        // 2) 타임아웃(ms) 지정
        factory.setConnectTimeout(5_000); // 서버에 연결 시도 제한 (ms)
        factory.setReadTimeout(5_000);    // 응답 본문 읽기 제한 (ms)

        // 3) RestTemplate에 팩토리 주입
        return builder
                .requestFactory(() -> factory)
                .build();
    }
}
