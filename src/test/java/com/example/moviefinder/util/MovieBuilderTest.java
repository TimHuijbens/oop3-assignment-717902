package com.example.moviefinder.util;

import com.example.moviefinder.model.Movie;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MovieBuilderTest {

    private final MovieBuilder movieBuilder = new MovieBuilder();

    @Test
    void buildMovie_ShouldConstructValidMovie() {
        JSONObject omdbData = new JSONObject()
                .put("Title", "Inception")
                .put("Year", "2010")
                .put("Rated", "PG-13")
                .put("Released", "16 Jul 2010")
                .put("Runtime", "148 min")
                .put("Genre", "Action, Sci-Fi")
                .put("Director", "Christopher Nolan")
                .put("Actors", "Leonardo DiCaprio, Joseph Gordon-Levitt, Ellen Page")
                .put("Plot", "A thief who steals corporate secrets...")
                .put("Language", "English")
                .put("imdbRating", "8.8")
                .put("BoxOffice", "$829,895,144");

        JSONObject tmdbSearchResult = new JSONObject(); // unused
        JSONObject keywords = new JSONObject().put("keywords", new JSONArray()
                .put(new JSONObject().put("name", "dream"))
                .put(new JSONObject().put("name", "subconscious")));
        JSONObject similar = new JSONObject().put("results", new JSONArray()
                .put(new JSONObject().put("title", "The Matrix")));
        JSONObject providers = new JSONObject().put("results", new JSONObject()
                .put("US", new JSONObject().put("flatrate", new JSONArray()
                        .put(new JSONObject().put("provider_name", "Netflix")))));

        List<String> imagePaths = List.of("path1.jpg", "path2.jpg");

        Movie result = movieBuilder.buildMovie(omdbData, tmdbSearchResult, new JSONObject(), keywords, similar, providers, imagePaths);

        assertThat(result.getTitle()).isEqualTo("Inception");
        assertThat(result.getActors()).contains("Leonardo DiCaprio");
        assertThat(result.getKeywords()).contains("dream");
        assertThat(result.getSimilarMovies()).contains("The Matrix");
        assertThat(result.getWatchProviders()).contains("Netflix");
        assertThat(result.getImagePath1()).isEqualTo("path1.jpg");
    }
}