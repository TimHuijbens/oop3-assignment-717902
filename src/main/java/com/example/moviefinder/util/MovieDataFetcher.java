package com.example.moviefinder.util;

import com.example.moviefinder.exceptions.MovieDataFetchException;
import com.example.moviefinder.exceptions.TmdbApiException;
import com.example.moviefinder.model.Movie;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.*;

@Component
public class MovieDataFetcher {

    private final OmdbClient omdbClient;
    private final TmdbClient tmdbClient;
    private final ImageDownloader imageDownloader;
    private final MovieBuilder movieBuilder;
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    public MovieDataFetcher(OmdbClient omdbClient, TmdbClient tmdbClient,
                            ImageDownloader imageDownloader, MovieBuilder movieBuilder) {
        this.omdbClient = omdbClient;
        this.tmdbClient = tmdbClient;
        this.imageDownloader = imageDownloader;
        this.movieBuilder = movieBuilder;
    }

    public Movie fetchAndBuildMovie(String titleInput) {
        try {
            // Step 1: Fetch data
            JSONObject omdbData = omdbClient.fetchMovieData(titleInput); // May throw OmdbApiException
            JSONObject tmdbSearch = tmdbClient.searchMovie(titleInput);  // May throw TmdbApiException

            if (tmdbSearch == null || tmdbSearch.getJSONArray("results").isEmpty()) {
                throw new TmdbApiException("No TMDB results found for title: " + titleInput);
            }

            JSONObject tmdbResult = tmdbSearch.getJSONArray("results").getJSONObject(0);
            int tmdbId = tmdbResult.getInt("id");

            // Step 2: Fetch other details in parallel
            Future<JSONObject> imagesFuture = executor.submit(() -> tmdbClient.fetchMovieDetails(tmdbId, "images"));
            Future<JSONObject> keywordsFuture = executor.submit(() -> tmdbClient.fetchMovieDetails(tmdbId, "keywords"));
            Future<JSONObject> similarFuture = executor.submit(() -> tmdbClient.fetchMovieDetails(tmdbId, "similar"));
            Future<JSONObject> providersFuture = executor.submit(() -> tmdbClient.fetchMovieDetails(tmdbId, "watch/providers"));

            JSONObject images = imagesFuture.get();     // May throw ExecutionException
            JSONObject keywords = keywordsFuture.get();
            JSONObject similar = similarFuture.get();
            JSONObject providers = providersFuture.get();

            // Step 3: Build and return movie
            List<String> imagePaths = imageDownloader.downloadImages(images, omdbData.getString("Title"));
            return movieBuilder.buildMovie(omdbData, tmdbResult, images, keywords, similar, providers, imagePaths);

        } catch (ExecutionException e) {
            // If one of the futures failed, unwrap the cause
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause; // Let GlobalExceptionHandler deal with it
            }
            throw new MovieDataFetchException("Error during parallel data fetching", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new MovieDataFetchException("Thread interrupted during data fetch", e);
        }
    }
}