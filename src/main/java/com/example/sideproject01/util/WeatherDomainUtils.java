package com.example.sideproject01.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * 날씨 도메인 공통 유틸(순수 함수 모음)
 * - 상태 없음(스레드 세이프), 어디서든 재사용 가능
 * - 서비스/컨트롤러/업서트 로직에서 공통 규칙을 한 곳에서 관리
 */
public final class WeatherDomainUtils {

    // 공통 시간대 (KST)
    public static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private WeatherDomainUtils() { /* 인스턴스화 금지 */ }

    /** 외부 응답의 온도가 NaN인 경우 0.0으로 보정 */
    public static double safeTemp(double v) {
        return Double.isNaN(v) ? 0.0 : v;
    }

    /**
     * 강수확률(0.0~1.0)을 0.1 단위로 반올림해 0.0~1.0로 반환
     * - null → 0.0
     * - 0.04 → 0.0, 0.05 → 0.1, 0.96 → 1.0
     */
    public static double normalizePop01(Double pop) {
        if (pop == null) return 0.0;
        double rounded = Math.round(pop * 10.0) / 10.0;
        if (rounded < 0.0) return 0.0;
        if (rounded > 1.0) return 1.0;
        return rounded;
    }

    /**
     * 강수확률(0.0~1.0)을 10% 단위 정수 퍼센트로 반환 (0,10,...,100)
     * - UI에 바로 쓰기 편함
     */
    public static int normalizePopPct(Double pop) {
        double v = normalizePop01(pop);
        int pct = (int) Math.round(v * 100.0);
        return Math.max(0, Math.min(100, pct));
    }

    /** UTC epoch seconds → KST 자정 기준 LocalDate */
    public static LocalDate toKstDate(long utcEpochSeconds) {
        return Instant.ofEpochSecond(utcEpochSeconds).atZone(KST).toLocalDate();
    }
}
