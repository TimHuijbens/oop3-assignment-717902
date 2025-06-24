package com.example.moviefinder.util;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TmdbClientTest {

    @Autowired
    private TmdbClient tmdbClient;

    @Test
    void searchMovie_ShouldReturnJsonWithResults() throws Exception {
        JSONObject result = tmdbClient.searchMovie("Inception");
        assertThat(result.getJSONArray("results").length()).isGreaterThan(0);
    }

    @Test
    void fetchMovieDetails_ShouldReturnImagesJson() throws Exception {
        JSONObject details = tmdbClient.fetchMovieDetails(27205, "images"); // TMDb ID for Inception
        assertThat(details.has("backdrops")).isTrue();
    }
}