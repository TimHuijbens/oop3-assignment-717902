package com.example.moviefinder.util;

import com.example.moviefinder.exceptions.OmdbApiException;
import com.example.moviefinder.exceptions.TmdbApiException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ExceptionClassTest {

    @Test
    void omdbApiException_ShouldStoreMessageAndCause() {
        Exception cause = new RuntimeException("Network down");
        OmdbApiException ex = new OmdbApiException("OMDb failure", cause);

        assertThat(ex.getMessage()).isEqualTo("OMDb failure");
        assertThat(ex.getCause()).isEqualTo(cause);
    }

    @Test
    void tmdbApiException_ShouldStoreMessageAndCause() {
        Exception cause = new RuntimeException("Timeout");
        TmdbApiException ex = new TmdbApiException("TMDb failure", cause);

        assertThat(ex.getMessage()).isEqualTo("TMDb failure");
        assertThat(ex.getCause()).isEqualTo(cause);
    }
}