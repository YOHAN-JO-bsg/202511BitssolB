// src/main/java/com/example/sideproject01/cache/GeoKey.java
package com.example.sideproject01.cache;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 	GeoKey
 *  - 캐시 키를 "항상 같은 규칙"으로 만들어 주는 유틸리티.
 *  - 좌표(lat, lon)를 소수점 1자리로 "격자화"해서 키 다양성을 줄인다.
 *    예) 37.5665 → 37.6, 126.9780 → 127.0
 *    왜? 거의 같은 위치 요청이 캐시에 서로 다른 키로 쌓이는 걸 방지(캐시 효율 ↑).
 *
 *  - 정적(static) 메서드만 제공하는 "순수 함수" 설계:
 *    → 스프링 @Cacheable의 SpEL에서 바로 호출 가능.
 *
 *  - 키 포맷: "onecall:<격자화된위도>,<격자화된경도>"
 *    예) onecall:37.6,127.0
 *
 *  - 나중에 키 포맷을 바꿔야 할 일이 생겨도,
 *    이 클래스만 바꾸면 서비스/컨트롤러는 그대로 유지 가능(결합도 ↓).
 */
public final class GeoKey {

    private GeoKey() {} // 인스턴스화 금지(유틸리티 클래스 관례)

    /**
     * grid1
     *  - 소수점 1자리 반올림(HALF_UP).
     *  - 37.54 → 37.5, 37.55 → 37.6
     */
    public static double grid1(double v) {
        return new BigDecimal(v)
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue();
    }

    /**
     * oneCallKey
     *  - One Call API 전용 키 생성기.
     *  - 좌표를 0.1도 격자로 반올림 → "onecall:<lat>,<lon>" 포맷 문자열 반환.
     *  - 추후 units/lang 등을 키에 포함하고 싶다면 이 메서드에만 추가하면 된다.
     */
    public static String oneCallKey(double lat, double lon) {
        double gLat = grid1(lat);
        double gLon = grid1(lon);
        return String.format("onecall:%.1f,%.1f", gLat, gLon);
    }
}
