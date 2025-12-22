// src/main/java/com/example/sideproject01/client/OpenWeatherClient.java
package com.example.sideproject01.client;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.example.sideproject01.config.WeatherApiProperties;
import com.example.sideproject01.exception.WeatherApiException;

import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.util.List;

/**
 * OpenWeather 외부 호출 클라이언트 (무료 플랜용)
 * - /data/2.5/forecast (5일/3시간 간격) 호출
 * - 서비스 레이어에서 "일별"로 집계해서 프런트에 맞는 DTO로 변환
 */
@Component
@RequiredArgsConstructor
public class OpenWeatherClient {

    private final RestTemplate restTemplate;
    private final WeatherApiProperties props; 
    

    /**
     * 5일/3시간 간격 예보 조회 (/data/2.5/forecast)
     * - 무료 플랜에서 사용 가능
     */
    public Forecast5dResponse getForecast5d(double lat, double lon) {
        String url = String.format(
                "%s/forecast?lat=%f&lon=%f&appid=%s&units=%s&lang=%s",
                props.getBaseUrl(), lat, lon, props.getKey(), props.getUnits(), props.getLang()
        );

        RequestEntity<Void> req = RequestEntity.get(URI.create(url)).build();
        try {
            ResponseEntity<Forecast5dResponse> res = restTemplate.exchange(req, Forecast5dResponse.class);

            if (!res.getStatusCode().is2xxSuccessful() || res.getBody() == null) {
                throw new WeatherApiException("Upstream weather API error: " + res.getStatusCode());
            }
            return res.getBody();
        } catch (RestClientResponseException ex) {
            // OpenWeather가 4xx/5xx를 주면 RestTemplate이 이 예외를 던짐 → 우리 도메인 예외로 감싸 전역 핸들러가 502를 반환하게 함
            String msg = String.format("OpenWeather error %d %s", ex.getRawStatusCode(), ex.getStatusText());
            throw new WeatherApiException(msg, ex);
        } catch (RestClientException ex) {
            // 연결 실패/타임아웃 등 다른 RestTemplate 오류도 WeatherApiException으로 통일
            throw new WeatherApiException("OpenWeather request failed", ex);
        }
    }

    // -------- /forecast 응답 중 필요한 필드만 캡쳐한 내부 전용 최소 모델 --------
    public static class Forecast5dResponse {
        public List<Item> list; // 3시간 간격 아이템들

        public static class Item {
            public long dt;                 // UTC epoch seconds
            public Main main;               // 온도
            public List<Weather> weather;   // 첫 요소의 icon/description 사용
            public Double pop;              // 0.0 ~ 1.0
        }
        public static class Main {
            public double temp;
            public Double feels_like;
        }
        public static class Weather {
            public String icon;             // e.g. "01d"
            public String description;      // lang=kr 설정 시 한글
        }
    }
}
