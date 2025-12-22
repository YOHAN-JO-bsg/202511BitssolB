// src/main/java/com/example/sideproject01/service/WeatherService.java
package com.example.sideproject01.service;

import com.example.sideproject01.dto.OneCallResponseDto;

/**
 * 날씨 도메인 서비스 인터페이스
 * - 컨트롤러는 이 인터페이스만 의존합니다.
 * - 구현체 교체(실 API ↔ 스텁/모킹)도 수월해집니다.
 */
public interface WeatherService {

    /**
     * OpenWeather OneCall(3.0) 호출 결과를 (캐시 적용 후) 반환
     * @param lat 위도
     * @param lon 경도
     * @return 일별 예보 리스트 DTO
     */
    public OneCallResponseDto getOneCallCached(double lat, double lon);
}
