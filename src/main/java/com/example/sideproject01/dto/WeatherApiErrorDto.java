// src/main/java/com/example/sideproject01/dto/WeatherApiErrorDto.java
package com.example.sideproject01.dto;

import java.time.OffsetDateTime;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class WeatherApiErrorDto {
    int status;                // HTTP status code
    String error;              // "Bad Request" ...
    String message;            // 상세 메시지(운영에선 민감정보 주의)
    String path;               // 요청 경로
    OffsetDateTime timestamp;  // 생성 시각
}
