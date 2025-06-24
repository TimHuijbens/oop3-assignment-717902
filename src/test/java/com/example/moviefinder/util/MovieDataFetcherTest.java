package com.example.moviefinder.util;

import com.example.moviefinder.model.Movie;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class MovieDataFetcherTest {

    private OmdbClient omdbClient;
    private TmdbClient tmdbClient;
    private ImageDownloader imageDownloader;
    private MovieBuilder movieBuilder;
    private MovieDataFetcher fetcher;

    @BeforeEach
    void setup() {
        omdbClient = mock(OmdbClient.class);
        tmdbClient = mock(TmdbClient.class);
        imageDownloader = mock(ImageDownloader.class);
        movieBuilder = mock(MovieBuilder.class);

        fetcher = new MovieDataFetcher(omdbClient, tmdbClient, imageDownloader, movieBuilder);
    }

    @Test
    void fetchAndBuildMovie_ShouldReturnMovie() throws Exception {
        String title = "Inception";

        JSONObject omdbJson = new JSONObject().put("Title", title);

        JSONObject tmdbSearch = new JSONObject()
                .put("results", new JSONArray()
                        .put(new JSONObject().put("id", 1)));

        JSONObject mockObj = new JSONObject(); // for images, keywords, similar, providers

        when(omdbClient.fetchMovieData(title)).thenReturn(omdbJson);
        when(tmdbClient.searchMovie(title)).thenReturn(tmdbSearch);

        when(tmdbClient.fetchMovieDetails(1, "images")).thenReturn(mockObj);
        when(tmdbClient.fetchMovieDetails(1, "keywords")).thenReturn(mockObj);
        when(tmdbClient.fetchMovieDetails(1, "similar")).thenReturn(mockObj);
        when(tmdbClient.fetchMovieDetails(1, "watch/providers")).thenReturn(mockObj);

        when(imageDownloader.downloadImages(mockObj, title)).thenReturn(List.of("img1", "img2"));

        Movie mockMovie = Movie.builder().title(title).build();
        when(movieBuilder.buildMovie(eq(omdbJson), any(), any(), any(), any(), any(), any()))
                .thenReturn(mockMovie);

        // Act
        Movie result = fetcher.fetchAndBuildMovie(title);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(title);
    }
}