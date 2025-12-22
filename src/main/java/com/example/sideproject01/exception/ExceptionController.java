package com.example.sideproject01.exception;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.swagger.v3.oas.annotations.Hidden;


@Hidden // swagger ui 에서 무시하도록 @Hidden
@RestControllerAdvice
public class ExceptionController {
   
   //@Valid 어노테이션을 이용해서 검증을 하다가 검증을 통과하지 못하면 여기가 실행된다
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
       // dto 에 필드 검증에 작성한 메시지 정보가 에러 정보에 담겨서 응답되도록 한다       
        var errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        DefaultMessageSourceResolvable::getDefaultMessage,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
        Map<String, Object> body = Map.of(
                "code", "VALIDATION_ERROR",
                "errors", errors
        );
        return ResponseEntity.badRequest().body(body);
    }   
}
