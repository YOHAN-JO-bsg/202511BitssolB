// src/main/java/com/example/sideproject01/dto/OneCallResponseDto.java
package com.example.sideproject01.dto;

import java.util.List;

import lombok.Value;

/**
 * OneCall 결과 중, 사용하는 "daily"만 뽑아서 담는 응답 DTO
 */
@Value
public class OneCallResponseDto{
        List<OneCallDailyDto> daily;
}
