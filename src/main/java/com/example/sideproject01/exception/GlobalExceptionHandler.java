// src/main/java/com/example/sideproject01/exception/GlobalExceptionHandler.java
package com.example.sideproject01.exception;

import java.time.OffsetDateTime;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.example.sideproject01.dto.WeatherApiErrorDto;

import org.springframework.http.converter.HttpMessageNotReadableException;


// 날씨 API 전역 예외 처리 
@RestControllerAdvice(basePackages = "com.example.sideproject01.controller")
public class GlobalExceptionHandler {

    // 400: Bean Validation 위반(@DecimalMin/@DecimalMax 등)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<WeatherApiErrorDto> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req.getRequestURI());
    }

    // 400: @RequestParam 타입 불일치 등
    @ExceptionHandler({
        MethodArgumentTypeMismatchException.class,
        MissingServletRequestParameterException.class,
        HttpMessageNotReadableException.class,
        MethodArgumentNotValidException.class
    })
    public ResponseEntity<WeatherApiErrorDto> handleBadRequest(Exception ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req.getRequestURI());
    }

    // 502: 외부(OpenWeather) 호출 실패
    @ExceptionHandler(WeatherApiException.class)
    public ResponseEntity<WeatherApiErrorDto> handleUpstream(WeatherApiException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_GATEWAY, ex.getMessage(), req.getRequestURI());
    }

    // 500: 그 외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<WeatherApiErrorDto> handleAny(Exception ex, HttpServletRequest req) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", req.getRequestURI());
    }

    private ResponseEntity<WeatherApiErrorDto> build(HttpStatus status, String msg, String path) {
    	WeatherApiErrorDto body = WeatherApiErrorDto.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(msg)
                .path(path)
                .timestamp(OffsetDateTime.now())
                .build();
        return ResponseEntity.status(status).body(body);
    }
}
