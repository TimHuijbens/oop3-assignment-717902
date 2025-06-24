package com.example.moviefinder.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for all controllers in the application.
 * Catches specific and generic exceptions and formats error responses uniformly.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles exceptions related to general movie data fetching failures.
     *
     * @param ex the thrown {@link MovieDataFetchException}
     * @return a 400 Bad Request error response with details
     */
    @ExceptionHandler(MovieDataFetchException.class)
    public ResponseEntity<Map<String, Object>> handleMovieDataFetchException(MovieDataFetchException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Failed to fetch movie data", ex.getMessage());
    }

    /**
     * Handles OMDb API-specific failures.
     *
     * @param ex the thrown {@link OmdbApiException}
     * @return a 400 Bad Request error response with details
     */
    @ExceptionHandler(OmdbApiException.class)
    public ResponseEntity<Map<String, Object>> handleOmdbError(OmdbApiException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "OMDb API error", ex.getMessage());
    }

    /**
     * Handles TMDb API-specific failures.
     *
     * @param ex the thrown {@link TmdbApiException}
     * @return a 400 Bad Request error response with details
     */
    @ExceptionHandler(TmdbApiException.class)
    public ResponseEntity<Map<String, Object>> handleTmdbError(TmdbApiException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "TMDb API error", ex.getMessage());
    }

    /**
     * Handles parameter type mismatches in requests.
     *
     * @param ex the thrown {@link MethodArgumentTypeMismatchException}
     * @return a 400 Bad Request error response indicating invalid parameter types
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid parameter type", ex.getMessage());
    }

    /**
     * Handles uncaught exceptions and returns a generic error response.
     *
     * @param ex any thrown {@link Exception}
     * @return a 500 Internal Server Error response with a generic message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", ex.getMessage());
    }

    /**
     * Helper method to build a standard error response body.
     *
     * @param status  the HTTP status code
     * @param error   a short error description
     * @param message a detailed error message
     * @return a formatted {@link ResponseEntity} with error details
     */
    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String error, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);

        return ResponseEntity.status(status).body(body);
    }
}