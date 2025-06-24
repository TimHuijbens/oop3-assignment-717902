package com.example.moviefinder.util;

import com.example.moviefinder.exceptions.TmdbApiException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Client component for interacting with The Movie Database (TMDb) API.
 * <p>
 * Provides methods to search for movies and fetch additional movie-related data (e.g., images, keywords, providers).
 * Wraps network and API-level failures in {@link TmdbApiException}.
 */
@Component
public class TmdbClient {

    private final RestTemplate restTemplate;
    private final String tmdbApiKey;
    private boolean forceFailure = false; // For test simulation purposes only

    /**
     * Constructs a new {@code TmdbClient} using the provided {@link RestTemplate} and TMDb API key.
     *
     * @param restTemplate the RestTemplate used for HTTP calls
     * @param tmdbApiKey   the API key injected from application properties
     */
    public TmdbClient(RestTemplate restTemplate,
                      @Value("${tmdb.api.key}") String tmdbApiKey) {
        this.restTemplate = restTemplate;
        this.tmdbApiKey = tmdbApiKey;
    }

    /**
     * Simulates a failure for testing by forcing any TMDb call to throw a {@link TmdbApiException}.
     * Useful in integration tests to validate error handling behavior.
     */
    public void enableFailureSimulation() {
        this.forceFailure = true;
    }

    /**
     * Performs a movie search query using the TMDb API.
     *
     * @param title the title of the movie to search for
     * @return a {@link JSONObject} representing the TMDb API response
     * @throws TmdbApiException if the request fails or if failure simulation is enabled
     */
    public JSONObject searchMovie(String title) {
        if (forceFailure) {
            throw new TmdbApiException("Simulated TMDb failure for testing");
        }

        try {
            String url = "https://api.themoviedb.org/3/search/movie?api_key=" + tmdbApiKey + "&query=" + title;
            String response = restTemplate.getForObject(url, String.class);
            return new JSONObject(response);
        } catch (RestClientException e) {
            throw new TmdbApiException("Failed to fetch data from TMDb for title: " + title, e);
        }
    }

    /**
     * Fetches detailed data for a specific movie ID from the TMDb API.
     *
     * @param id   the TMDb movie ID
     * @param type the type of detail to fetch (e.g., {@code images}, {@code keywords}, {@code similar}, {@code watch/providers})
     * @return a {@link JSONObject} containing the requested movie detail
     * @throws TmdbApiException if the request fails
     */
    public JSONObject fetchMovieDetails(int id, String type) {
        try {
            String url = "https://api.themoviedb.org/3/movie/" + id + "/" + type + "?api_key=" + tmdbApiKey;
            String response = restTemplate.getForObject(url, String.class);
            return new JSONObject(response);
        } catch (RestClientException e) {
            throw new TmdbApiException("Failed to fetch " + type + " from TMDb for id: " + id, e);
        }
    }
}