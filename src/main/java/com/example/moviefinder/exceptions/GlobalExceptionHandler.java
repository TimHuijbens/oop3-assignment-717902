package com.example.moviefinder.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MovieDataFetchException.class)
    public ResponseEntity<Map<String, Object>> handleMovieDataFetchException(MovieDataFetchException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Failed to fetch movie data", ex.getMessage());
    }

    @ExceptionHandler(OmdbApiException.class)
    public ResponseEntity<Map<String, Object>> handleOmdbError(OmdbApiException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "OMDb API error", ex.getMessage());
    }

    @ExceptionHandler(TmdbApiException.class)
    public ResponseEntity<Map<String, Object>> handleTmdbError(TmdbApiException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "TMDb API error", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid parameter type", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String error, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);

        return ResponseEntity.status(status).body(body);
    }
}