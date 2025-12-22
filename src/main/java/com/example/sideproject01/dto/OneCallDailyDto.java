// src/main/java/com/example/sideproject01/dto/OneCallDailyDto.java
package com.example.sideproject01.dto;

import lombok.Value;

/**
 * 프론트/서비스에서 쓰기 편하도록 필요한 필드만 담은 DTO
 * - dt: UNIX epoch seconds (UTC)
 * - tempDay: 낮 기온(섭씨, units=metric)
 * - icon: 날씨 아이콘 코드 (예: "01d")
 * - pop: 강수확률(0.0 ~ 1.0) ; 서비스에서 0/10/…/100으로 가공 예정
 */
@Value
public class OneCallDailyDto{
        long dt;
        double tempDay;
        String icon;
        Double pop;
}
