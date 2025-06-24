package com.example.moviefinder.exceptions;

/**
 * Exception thrown when an error occurs while communicating with the TMDb API.
 */
public class TmdbApiException extends RuntimeException {

    /**
     * Constructs a new TmdbApiException with the specified detail message.
     *
     * @param message the detail message explaining the TMDb error
     */
    public TmdbApiException(String message) {
        super(message);
    }

    /**
     * Constructs a new TmdbApiException with the specified detail message and underlying cause.
     *
     * @param message the detail message explaining the TMDb error
     * @param cause   the underlying exception that triggered this error
     */
    public TmdbApiException(String message, Throwable cause) {
        super(message, cause);
    }
}