package com.example.moviefinder.exceptions;

public class OmdbApiException extends RuntimeException {
    public OmdbApiException(String message) {
        super(message);
    }

    public OmdbApiException(String message, Throwable cause) {
        super(message, cause);
    }
}