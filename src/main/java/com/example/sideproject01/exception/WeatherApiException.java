// src/main/java/com/example/sideproject01/exception/WeatherApiException.java
package com.example.sideproject01.exception;

public class WeatherApiException extends RuntimeException {
    public WeatherApiException(String message) { super(message); }
    public WeatherApiException(String message, Throwable cause) { super(message, cause); }
}
