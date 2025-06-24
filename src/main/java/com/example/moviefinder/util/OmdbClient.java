package com.example.moviefinder.util;

import com.example.moviefinder.exceptions.OmdbApiException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Client component responsible for communicating with the OMDb API to retrieve movie metadata.
 * <p>
 * Uses {@link RestTemplate} to perform HTTP GET requests and parses the response as JSON.
 * Handles error responses from the API and wraps exceptions in a custom {@link OmdbApiException}.
 */
@Component
public class OmdbClient {

    /**
     * API key for authenticating with the OMDb API, loaded from the application properties.
     */
    @Value("${omdb.api.key}")
    private String omdbApiKey;

    private final RestTemplate restTemplate;

    /**
     * Constructs the OmdbClient with a provided {@link RestTemplate}.
     *
     * @param restTemplate injected RestTemplate used to make HTTP requests
     */
    public OmdbClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Fetches metadata for a given movie title from the OMDb API.
     *
     * @param title the movie title to search for
     * @return a {@link JSONObject} containing OMDb metadata
     * @throws OmdbApiException if the API response indicates failure or the request fails
     */
    public JSONObject fetchMovieData(String title) {
        try {
            String url = "http://www.omdbapi.com/?t=" + title + "&apikey=" + omdbApiKey;
            String response = restTemplate.getForObject(url, String.class);
            JSONObject json = new JSONObject(response);

            // OMDb API returns { "Response": "False", "Error": "Movie not found!" } on failure
            if (!json.optString("Response", "True").equalsIgnoreCase("True")) {
                throw new OmdbApiException("OMDb error: " + json.optString("Error", "Unknown error"));
            }

            return json;
        } catch (RestClientException e) {
            throw new OmdbApiException("Failed to fetch data from OMDb for title: " + title, e);
        }
    }
}