package com.example.moviefinder.exceptions;

/**
 * Exception thrown when an error occurs while communicating with the OMDb API.
 */
public class OmdbApiException extends RuntimeException {

    /**
     * Constructs a new OmdbApiException with the specified detail message.
     *
     * @param message the detail message explaining the cause of the error
     */
    public OmdbApiException(String message) {
        super(message);
    }

    /**
     * Constructs a new OmdbApiException with the specified detail message and underlying cause.
     *
     * @param message the detail message explaining the cause of the error
     * @param cause   the underlying exception that triggered this error
     */
    public OmdbApiException(String message, Throwable cause) {
        super(message, cause);
    }
}