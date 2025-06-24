package com.example.moviefinder.util;

import com.example.moviefinder.model.Movie;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Component
public class MovieDataFetcher {

    private final OmdbClient omdbClient;
    private final TmdbClient tmdbClient;
    private final ImageDownloader imageDownloader;
    private final MovieBuilder movieBuilder;
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    public MovieDataFetcher(OmdbClient omdbClient,
                            TmdbClient tmdbClient,
                            ImageDownloader imageDownloader,
                            MovieBuilder movieBuilder) {
        this.omdbClient = omdbClient;
        this.tmdbClient = tmdbClient;
        this.imageDownloader = imageDownloader;
        this.movieBuilder = movieBuilder;
    }

    public Movie fetchAndBuildMovie(String titleInput) throws Exception {
        Future<JSONObject> omdbFuture = executor.submit(() -> omdbClient.fetchMovieData(titleInput));
        Future<JSONObject> tmdbSearchFuture = executor.submit(() -> tmdbClient.searchMovie(titleInput));

        JSONObject omdbData = omdbFuture.get();
        JSONObject tmdbSearch = tmdbSearchFuture.get();

        if (omdbData == null || tmdbSearch == null || tmdbSearch.getJSONArray("results").isEmpty()) {
            throw new IllegalStateException("Failed to fetch movie data.");
        }

        JSONObject tmdbResult = tmdbSearch.getJSONArray("results").getJSONObject(0);
        int tmdbId = tmdbResult.getInt("id");

        Future<JSONObject> imagesFuture = executor.submit(() -> tmdbClient.fetchMovieDetails(tmdbId, "images"));
        Future<JSONObject> keywordsFuture = executor.submit(() -> tmdbClient.fetchMovieDetails(tmdbId, "keywords"));
        Future<JSONObject> similarFuture = executor.submit(() -> tmdbClient.fetchMovieDetails(tmdbId, "similar"));
        Future<JSONObject> providersFuture = executor.submit(() -> tmdbClient.fetchMovieDetails(tmdbId, "watch/providers"));

        JSONObject images = imagesFuture.get();
        JSONObject keywords = keywordsFuture.get();
        JSONObject similar = similarFuture.get();
        JSONObject providers = providersFuture.get();

        List<String> imagePaths = imageDownloader.downloadImages(images, omdbData.getString("Title"));

        return movieBuilder.buildMovie(omdbData, tmdbResult, images, keywords, similar, providers, imagePaths);
    }
}