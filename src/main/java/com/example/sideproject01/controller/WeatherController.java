// src/main/java/com/example/sideproject01/controller/WeatherController.java
package com.example.sideproject01.controller;

import com.example.sideproject01.service.WeatherService;

import io.swagger.v3.oas.annotations.tags.Tag;

import com.example.sideproject01.dto.OneCallResponseDto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 	날씨 조회용 컨트롤러
 * 	- 역할: 파라미터 검증 → 서비스 호출 → DTO 반환
 * 	- CORS는 CorsConfig에서 /api/** 로 이미 허용됨
 *
 * 엔드포인트:
 *   GET /api/weather/onecall?lat=37.5665&lon=126.9780
 *   - 같은 좌표 2번 호출 시, 2번째는 캐시 히트로 더 빠름
 */
@RestController
@RequestMapping("/api/weather")
@Validated // @Min/@Max 등 Bean Validation 활성화
@RequiredArgsConstructor
@Tag(name = "Weather", description = "Weather API") 
public class WeatherController {

    private final WeatherService weatherService; // 인터페이스 의존


    /**
     * One Call 3.0 기반 일별 예보 조회(캐시 적용)
     *
     * 검증:
     *  - 위도(lat): -90 ~ 90
     *  - 경도(lon): -180 ~ 180
     *
     * 응답:
     *  - 200 OK: OneCallResponseDto (우리 앱 전용 간결 DTO)
     *  - 400 Bad Request: 파라미터 범위 오류
     *  - 502 Bad Gateway: 외부 API 오류(네트워크/429 등)
     */
    @GetMapping("/onecall")
    public ResponseEntity<OneCallResponseDto> onecall(
    		// 기본값으로 서울의 위치를 지정 
    		@RequestParam(defaultValue = "37.5665") @DecimalMin("-90.0")  @DecimalMax("90.0")  double lat,
    		@RequestParam(defaultValue = "126.9780") @DecimalMin("-180.0") @DecimalMax("180.0") double lon
    ) {
        // 서비스가 캐시와 도메인 정규화를 담당
        OneCallResponseDto body = weatherService.getOneCallCached(lat, lon);
        return ResponseEntity.ok(body);
    }

    
}
