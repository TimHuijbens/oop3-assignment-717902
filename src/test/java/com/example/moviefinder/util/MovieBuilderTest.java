package com.example.moviefinder.util;

import com.example.moviefinder.model.Movie;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MovieBuilderTest {

    private MovieBuilder movieBuilder;

    @BeforeEach
    void setUp() {
        movieBuilder = new MovieBuilder();
    }

    @Test
    void buildMovie_ShouldPopulateFieldsCorrectly() {
        // Arrange: Mock OMDB data
        JSONObject omdbData = new JSONObject()
                .put("Title", "Inception")
                .put("Year", "2010")
                .put("Rated", "PG-13")
                .put("Released", "16 Jul 2010")
                .put("Runtime", "148 min")
                .put("Genre", "Action, Sci-Fi")
                .put("Director", "Christopher Nolan")
                .put("Actors", "Leonardo DiCaprio, Joseph Gordon-Levitt, Ellen Page")
                .put("Plot", "A thief steals corporate secrets through dream-sharing.")
                .put("Language", "English")
                .put("imdbRating", "8.8")
                .put("BoxOffice", "$829.9M");

        // TMDB mock responses
        JSONObject keywords = new JSONObject()
                .put("keywords", new JSONArray()
                        .put(new JSONObject().put("name", "dream"))
                        .put(new JSONObject().put("name", "subconscious"))
                        .put(new JSONObject().put("name", "mind-bending")));

        JSONObject similar = new JSONObject()
                .put("results", new JSONArray()
                        .put(new JSONObject().put("title", "The Matrix"))
                        .put(new JSONObject().put("title", "Interstellar")));

        JSONObject providers = new JSONObject()
                .put("results", new JSONObject()
                        .put("US", new JSONObject()
                                .put("flatrate", new JSONArray()
                                        .put(new JSONObject().put("provider_name", "Netflix"))
                                        .put(new JSONObject().put("provider_name", "Hulu")))));

        JSONObject images = new JSONObject(); // Not parsed directly here

        List<String> imagePaths = List.of("image1.jpg", "image2.jpg", "image3.jpg");

        // Act
        Movie result = movieBuilder.buildMovie(omdbData, new JSONObject(), images, keywords, similar, providers, imagePaths);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Inception");
        assertThat(result.getActors()).isEqualTo("Leonardo DiCaprio, Joseph Gordon-Levitt, Ellen Page");
        assertThat(result.getKeywords()).isEqualTo("dream, subconscious, mind-bending");
        assertThat(result.getSimilarMovies()).isEqualTo("The Matrix, Interstellar");
        assertThat(result.getWatchProviders()).isEqualTo("Netflix, Hulu");
        assertThat(result.getImagePath1()).isEqualTo("image1.jpg");
        assertThat(result.getImagePath2()).isEqualTo("image2.jpg");
        assertThat(result.getImagePath3()).isEqualTo("image3.jpg");
    }
}