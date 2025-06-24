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
    void searchMovie_ShouldReturnResults() throws Exception {
        JSONObject result = tmdbClient.searchMovie("Inception");

        assertThat(result).isNotNull();
        assertThat(result.getJSONArray("results")).isNotEmpty();
        assertThat(result.getJSONArray("results").getJSONObject(0).getString("title")).isNotEmpty();
    }

    @Test
    void fetchMovieDetails_ShouldReturnImages() throws Exception {
        // Get TMDB ID from search
        JSONObject searchResult = tmdbClient.searchMovie("Inception");
        int tmdbId = searchResult.getJSONArray("results").getJSONObject(0).getInt("id");

        // Fetch movie images
        JSONObject images = tmdbClient.fetchMovieDetails(tmdbId, "images");

        assertThat(images).isNotNull();
        assertThat(images.optJSONArray("backdrops")).isNotEmpty();
    }
}