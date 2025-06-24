package com.example.moviefinder.exceptions;

public class MovieDataFetchException extends RuntimeException {
    public MovieDataFetchException(String message) {
        super(message);
    }

    public MovieDataFetchException(String message, Throwable cause) {
        super(message, cause);
    }
}