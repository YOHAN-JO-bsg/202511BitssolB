// src/main/java/com/example/sideproject01/util/ForecastAggregationUtils.java
package com.example.sideproject01.util;

import com.example.sideproject01.client.OpenWeatherClient.Forecast5dResponse;
import com.example.sideproject01.dto.OneCallDailyDto;

import java.time.Instant;   // UNIX epoch(sec) -> 시간대 적용 가능한 객체
import java.time.LocalDate; // "연-월-일"만 필요할 때 사용
import java.time.ZoneId;    // 시간대(KST) 적용
import java.util.List;
import java.util.Map;
import java.util.Objects;    // null 체크 유틸
import java.util.TreeMap;   // 날짜 오름차순 정렬용(그룹 결과를 날짜 순으로 보관)
import java.util.stream.Collectors;

/**
 * 5일/3시간 간격(/forecast) 응답을 "일별 요약" 리스트로 변환하는 유틸.
 *
 *  1) OpenWeather /forecast 는 3시간 간격의 예보가 5일치 정도 온다 (예: 하루에 8개 슬롯)
 *  2) 각 슬롯(dt: epoch second)을 'KST(Asia/Seoul)' 기준 날짜(LocalDate)로 변환해서 "날짜별로 그룹핑"
 *  3) 날짜별로 다음을 계산:
 *     - 평균 온도 (main.temp 평균)
 *     - 평균 강수확률 pop (null 이면 0.0 취급) → 마지막에 도메인 규칙으로 0.1 단위 반올림
 *     - 최빈 아이콘 (가장 많이 나온 weather[0].icon) → 없으면 "01d" 기본값
 *  4) 각 날짜에 대해 OneCallDailyDto 생성
 *  5) /forecast 특성상 최대 5일치만 필요하므로 5개로 제한
 *
 *  - 스레드에 안전한 "순수 유틸" (상태 저장 X)
 *  - 시간대는 반드시 KST 적용 (서버 시간대/UTC와 무관하게 항상 한국 시간대 기준으로 날짜 산출)
 *  - null/빈 값 안전 처리 (main, pop, weather 리스트 등)
 */
public final class ForecastAggregationUtils {

    /** 한국 표준시(KST) 고정 사용 */
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private ForecastAggregationUtils() {
        /* 유틸 클래스 - 인스턴스화 방지 */
    }

    /**
     * /forecast(5일/3시간) 원본 응답을 "일별 요약 DTO 리스트"로 변환.
     *
     * @param raw OpenWeather /forecast 응답(3시간 단위 리스트 포함)
     * @return KST 기준 날짜 오름차순의 일별 요약 리스트(최대 5개)
     */
    public static List<OneCallDailyDto> toDaily(Forecast5dResponse raw) {

        // (0) 방어코드: null 또는 빈 데이터면 빈 리스트 반환
        if (raw == null || raw.list == null || raw.list.isEmpty()) {
            return List.of();
        }

        // (1) 3시간 슬롯들을 "KST 기준 LocalDate"로 그룹핑
        //     - key: LocalDate (예: 2025-10-10)
        //     - value: 그 날짜에 해당하는 3시간 슬롯들의 모음
        //     - TreeMap 사용 이유: 날짜 오름차순 유지(출력도 날짜 순으로)
        Map<LocalDate, List<Forecast5dResponse.Item>> byDate =
                raw.list.stream().collect(
                        Collectors.groupingBy(
                                // 그룹핑 기준: 각 슬롯의 epoch sec(dt)을 KST 로 변환해 LocalDate 추출
                                it -> Instant.ofEpochSecond(it.dt)   // 3시간 슬롯의 기준 시각(UTC 기준 sec)
                                            .atZone(KST)              // 한국 시간대 적용
                                            .toLocalDate(),           // 날짜만 사용(시/분/초 버림)
                                // Map 구현체를 TreeMap으로 지정 → 날짜 오름차순 유지
                                () -> new TreeMap<LocalDate, List<Forecast5dResponse.Item>>(),
                                // 같은 날짜로 모이는 값들은 리스트로 수집
                                Collectors.toList()
                        )
                );

        // (2) 날짜별 대표값 산출 → OneCallDailyDto 로 변환
        //     - avgTemp: 해당 날짜의 모든 슬롯 main.temp 평균
        //     - avgPop : 해당 날짜의 모든 슬롯 pop 평균 (null → 0.0 취급)
        //     - icon   : 해당 날짜의 모든 슬롯 중 가장 자주 등장한 weather[0].icon (없으면 "01d")
        //     - dt     : 그 날짜의 "KST 자정" epoch second (시작 기준점)
        return byDate.entrySet().stream()
                .map(entry -> {
                    // 그룹핑된 날짜(키)와 그 날짜의 슬롯들(값)
                    LocalDate date = entry.getKey();
                    List<Forecast5dResponse.Item> items = entry.getValue();

                    // (2-1) 평균 온도 계산
                    //  - main이 null이면 0.0 처리(최소한의 방어)
                    //  - Double 스트림으로 바꿔 평균 구함
                    double avgTemp = items.stream()
                            .map(i -> i.main == null ? 0.0 : i.main.temp) // Double 또는 double로 가정
                            .mapToDouble(d -> d.doubleValue())            // :: 없이 람다로 변환
                            .average()
                            .orElse(0.0);                                 // 모두 비정상이면 0.0

                    // (2-2) 평균 강수확률(pop) 계산
                    //  - OpenWeather pop: 0.0 ~ 1.0 (null 가능성 대비 0.0 처리)
                    double avgPop = items.stream()
                            .map(i -> i.pop == null ? 0.0 : i.pop)        // Double 또는 double
                            .mapToDouble(d -> d.doubleValue())
                            .average()
                            .orElse(0.0);

                    // (2-3) 최빈 아이콘(mode) 추출
                    //  - 각 슬롯의 weather 리스트가 비어있을 수 있으니 null 체크
                    //  - null 제거 후 (filter), 아이콘별 개수를 세어 가장 큰 값 선택
                    //  - 아무것도 없으면 기본 "01d" 사용 (맑음-주간 아이콘)
                    String icon = items.stream()
                            .map(i -> (i.weather != null && !i.weather.isEmpty())
                                       ? i.weather.get(0).icon
                                       : null)
                            .filter(x -> Objects.nonNull(x)) // :: 대신 람다로 null 제거
                            .collect(Collectors.groupingBy(
                                    x -> x,                    // 아이콘 문자열 자체로 그룹핑
                                    Collectors.counting()      // 등장 횟수 세기
                            ))
                            .entrySet().stream()
                            // Map.Entry<K,Long> 중 value(등장 수) 기준 최대값
                            .max(Map.Entry.comparingByValue())
                            .map(e -> e.getKey())             // 최빈 아이콘 문자열
                            .orElse("01d");                   // 기본값

                    // (2-4) 그 날짜의 "KST 자정(00:00:00)" epoch second → OneCallDailyDto의 dt로 사용
                    long kstMidnight = date.atStartOfDay(KST).toEpochSecond();

                    // (2-5) 도메인 규칙 적용
                    //  - safeTemp: 온도 보정(반올림/범위 제한 등 프로젝트 공통 규칙)
                    //  - normalizePop01: 0~1 pop을 0.1 단위로 반올림(예: 0.27 → 0.3)
                    return new OneCallDailyDto(
                            kstMidnight,
                            WeatherDomainUtils.safeTemp(avgTemp),
                            icon,
                            WeatherDomainUtils.normalizePop01(avgPop)
                    );
                })
                // (3) /forecast 특성상 최대 5일치만 사용 (UI/도메인 요구 조건에 맞춤)
                .limit(5)
                // (4) Stream → List 변환 (JDK 8 호환을 위해 Collectors 사용)
                .collect(Collectors.toList());
    }
}
