// src/main/java/com/example/sideproject01/service/WeatherServiceImpl.java
package com.example.sideproject01.service;

import com.example.sideproject01.client.OpenWeatherClient;
import com.example.sideproject01.client.OpenWeatherClient.Forecast5dResponse;
import com.example.sideproject01.dto.OneCallDailyDto;
import com.example.sideproject01.dto.OneCallResponseDto;
import com.example.sideproject01.util.ForecastAggregationUtils;
import com.example.sideproject01.util.WeatherDomainUtils;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 서비스 구현체는 인터페이스 메서드만 공개하고,
 * 상세 로직(집계/보정)은 유틸로 위임한다.
 */
@Service
@RequiredArgsConstructor
public class WeatherServiceImpl implements WeatherService {

    private final OpenWeatherClient client;


    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "owm", key = "'onecall:' + #lat + ':' + #lon")
    public OneCallResponseDto getOneCallCached(double lat, double lon) {
        // 1) 외부 호출 (무료 플랜: /data/2.5/forecast)
    	Forecast5dResponse raw = client.getForecast5d(lat, lon);

        // 2) 유틸로 "일별 DTO 리스트" 집계
    	List<OneCallDailyDto> daily = ForecastAggregationUtils.toDaily(raw);

        // 3) 래핑하여 반환
    	return new OneCallResponseDto(daily);
    }
}
