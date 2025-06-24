package com.example.moviefinder.util;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OmdbClientTest {

    @Autowired
    private OmdbClient omdbClient;

    @Test
    void fetchMovieData_ShouldReturnJson() throws Exception {
        JSONObject json = omdbClient.fetchMovieData("Inception");
        assertThat(json.optString("Title")).isEqualToIgnoringCase("Inception");
    }
}