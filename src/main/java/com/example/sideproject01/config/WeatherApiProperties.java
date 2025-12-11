// src/main/java/com/example/sideproject01/config/WeatherApiProperties.java
package com.example.sideproject01.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
/*
 * 어플리케이션 프로퍼티에서 값을 가져와 필드 선언해주기 
 */
@Getter
@RequiredArgsConstructor 
@ConfigurationProperties(prefix = "weather.api")
public class WeatherApiProperties {
    private final String baseUrl;
    private final String key;
    private final String units;
    private final String lang;
}
