package com.example.moviefinder.util;

import com.example.moviefinder.exceptions.TmdbApiException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class TmdbClient {

    private final RestTemplate restTemplate;
    private final String tmdbApiKey;

    private boolean forceFailure = false; // For testing purposes only

    public TmdbClient(RestTemplate restTemplate,
                      @Value("${tmdb.api.key}") String tmdbApiKey) {
        this.restTemplate = restTemplate;
        this.tmdbApiKey = tmdbApiKey;
    }

    public void enableFailureSimulation() {
        this.forceFailure = true;
    }

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