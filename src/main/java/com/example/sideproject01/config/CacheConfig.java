package com.example.sideproject01.config;

import java.time.Duration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public Caffeine<Object, Object> caffeineSpec() {
        return Caffeine.newBuilder()
                .maximumSize(5_000)                  // 캐시 항목 최대 수
                .expireAfterWrite(Duration.ofMinutes(10)) // 날씨는 10분 권장
                .recordStats();                      // (선택) 히트율/미스율 통계
    }

    @Bean
    public CacheManager cacheManager(Caffeine<Object, Object> caffeine) {
        // 필요 시 "geocode" 등 다른 캐시 이름 추가 가능
        CaffeineCacheManager manager = new CaffeineCacheManager("owm");
        manager.setCaffeine(caffeine);
        // manager.setAllowNullValues(false); // (선택) null 캐싱 방지
        return manager;
    }
}
