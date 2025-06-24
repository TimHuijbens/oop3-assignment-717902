package com.example.moviefinder.util;

import com.example.moviefinder.model.Movie;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MovieBuilderTest {

    private final MovieBuilder builder = new MovieBuilder();

    @Test
    void buildMovie_ShouldBuildExpectedMovie() {
        JSONObject omdb = new JSONObject()
                .put("Title", "Inception")
                .put("Year", "2010")
                .put("Actors", "Leonardo DiCaprio, Joseph Gordon-Levitt, Ellen Page");

        JSONObject search = new JSONObject().put("id", 123);
        JSONObject images = new JSONObject();
        JSONObject keywords = new JSONObject().put("keywords", new JSONArray().put(new JSONObject().put("name", "dream")));
        JSONObject similar = new JSONObject().put("results", new JSONArray().put(new JSONObject().put("title", "Interstellar")));
        JSONObject providers = new JSONObject().put("results", new JSONObject().put("US", new JSONObject()
                .put("flatrate", new JSONArray().put(new JSONObject().put("provider_name", "Netflix")))));

        Movie movie = builder.buildMovie(omdb, search, images, keywords, similar, providers, List.of("img1", "img2"));

        assertThat(movie.getTitle()).isEqualTo("Inception");
        assertThat(movie.getActors()).contains("Leonardo DiCaprio");
        assertThat(movie.getKeywords()).contains("dream");
        assertThat(movie.getWatchProviders()).contains("Netflix");
        assertThat(movie.getSimilarMovies()).contains("Interstellar");
    }
}