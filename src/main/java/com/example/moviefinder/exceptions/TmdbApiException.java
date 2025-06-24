package com.example.moviefinder.exceptions;

public class TmdbApiException extends RuntimeException {
    public TmdbApiException(String message) {
        super(message);
    }

    public TmdbApiException(String message, Throwable cause) {
        super(message, cause);
    }
}