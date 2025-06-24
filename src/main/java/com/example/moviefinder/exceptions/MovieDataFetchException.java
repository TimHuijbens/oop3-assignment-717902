package com.example.moviefinder.exceptions;

/**
 * Exception thrown when there is an issue fetching or building movie data
 * from external APIs or internal processing.
 */
public class MovieDataFetchException extends RuntimeException {

    /**
     * Constructs a new MovieDataFetchException with the specified detail message.
     *
     * @param message the detail message describing the reason for the exception
     */
    public MovieDataFetchException(String message) {
        super(message);
    }

    /**
     * Constructs a new MovieDataFetchException with the specified detail message and cause.
     *
     * @param message the detail message describing the reason for the exception
     * @param cause   the underlying cause of the exception
     */
    public MovieDataFetchException(String message, Throwable cause) {
        super(message, cause);
    }
}