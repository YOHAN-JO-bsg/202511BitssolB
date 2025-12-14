// src/main/java/com/example/sideproject01/rate/OwmRateGate.java
package com.example.sideproject01.rate;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 * OwmRateGate (임시 인메모리 레이트 리미터)
 *
 * 목적:
 * - 외부 API(예: OpenWeather) 호출 직전에 호출 횟수를 제한해
 *   무료 쿼터(예: 일 1000콜, 분당 60콜)를 넘지 않도록 보호한다.
 *
 * 특징:
 * - "고정 윈도우(fixed window)" 방식으로 매우 단순하게 동작한다.
 * - 분 단위 창과 일 단위 창을 따로 관리한다.
 * - 동시성 안전을 위해 ReentrantLock으로 임계영역을 보호한다.
 * - 단일 인스턴스에서만 정확하다(서버 재시작 시 카운터 초기화).
 *   → 사이드프로젝트엔 충분. 나중에 Bucket4j/Redis로 쉽게 교체 가능.
 *
 * 사용법:
 * - 외부 API를 호출하기 "직전"에 gate.acquireOrThrow()를 한 줄 호출한다.
 * - 초과 시 HTTP 429(Too Many Requests) 예외를 던져 컨트롤러까지 전파된다.
 *
 * 교체 용이성:
 * - 메서드 시그니처(acquireOrThrow)를 그대로 두고 내부 구현만 Bucket4j로 바꾸면,
 *   서비스/컨트롤러 코드는 손댈 필요가 없다.
 */
@Component
public class OwmRateGate {

    /** 분당 허용 콜 수 (버스트 보호). 필요하면 properties로 뺄 수 있음. */
    private static final long MINUTE_LIMIT = 60;

    /** 일일 허용 콜 수 (무료 한도 보호). 필요하면 properties로 뺄 수 있음. */
    private static final long DAY_LIMIT = 999;

    /** 동시성 제어용 Lock (멀티 스레드 환경에서 카운터 일관성 보장) */
    private final ReentrantLock lock = new ReentrantLock();

    /** "분 단위" 창의 시작 시각 */
    private Instant minuteWindowStart = Instant.now();
    /** 현재 분 창에서 사용한 호출 횟수 */
    private long minuteCount = 0;

    /** "일 단위(24시간)" 창의 시작 시각 */
    private Instant dayWindowStart = Instant.now();
    /** 현재 일 창에서 사용한 호출 횟수 */
    private long dayCount = 0;

    /**
     * 외부 API 호출 직전에 반드시 호출할 메서드.
     * - 한 번 호출할 때마다 호출 카운트를 1 증가시킨다.
     * - 증가 전, 현재 시각 기준으로 '분 창'과 '일 창'을 필요 시 초기화한다.
     * - 증가 후, 한도를 넘으면 HTTP 429를 던져 흐름을 차단한다.
     */
    public void acquireOrThrow() {
        lock.lock(); // 카운터/시각 갱신은 원자적으로 처리
        try {
            Instant now = Instant.now();

            // 1) "분 단위 창" 경과 체크: 1분 이상 지났다면 창을 초기화
            if (Duration.between(minuteWindowStart, now).compareTo(Duration.ofMinutes(1)) >= 0) {
                minuteWindowStart = now;
                minuteCount = 0;
            }

            // 2) "일 단위 창" 경과 체크: 24시간 이상 지났다면 창을 초기화
            if (Duration.between(dayWindowStart, now).compareTo(Duration.ofDays(1)) >= 0) {
                dayWindowStart = now;
                dayCount = 0;
            }

            // 3) 증가 후 한도 초과 여부 검사 (미리 +1 해보고 초과면 차단)
            if (minuteCount + 1 > MINUTE_LIMIT || dayCount + 1 > DAY_LIMIT) {
                // 무료 한도 보호: 초과 시 컨트롤러까지 429로 전파 → 클라이언트는 재시도/대기 처리
                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Weather API quota reached");
            }

            // 4) 안전하면 카운터 반영
            minuteCount++;
            dayCount++;

        } finally {
            lock.unlock();
        }
    }
}
