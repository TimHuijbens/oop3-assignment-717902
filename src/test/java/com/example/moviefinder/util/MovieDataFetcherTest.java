package com.example.moviefinder.util;

import com.example.moviefinder.model.Movie;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class MovieDataFetcherTest {

    private OmdbClient omdbClient;
    private TmdbClient tmdbClient;
    private ImageDownloader imageDownloader;
    private MovieBuilder movieBuilder;
    private MovieDataFetcher fetcher;

    @BeforeEach
    void setUp() {
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
        JSONObject searchResult = new JSONObject()
                .put("results", List.of(new JSONObject().put("id", 1234)));
        JSONObject dummyData = new JSONObject(); // minimal placeholder

        when(omdbClient.fetchMovieData(title)).thenReturn(omdbJson);
        when(tmdbClient.searchMovie(title)).thenReturn(searchResult);
        when(tmdbClient.fetchMovieDetails(1234, "images")).thenReturn(dummyData);
        when(tmdbClient.fetchMovieDetails(1234, "keywords")).thenReturn(dummyData);
        when(tmdbClient.fetchMovieDetails(1234, "similar")).thenReturn(dummyData);
        when(tmdbClient.fetchMovieDetails(1234, "watch/providers")).thenReturn(dummyData);
        when(imageDownloader.downloadImages(any(), eq(title))).thenReturn(List.of("image1.jpg"));
        Movie mockMovie = Movie.builder().title(title).build();
        when(movieBuilder.buildMovie(any(), any(), any(), any(), any(), any(), any())).thenReturn(mockMovie);

        Movie result = fetcher.fetchAndBuildMovie(title);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Inception");
    }
}