package com.example.moviefinder.util;

import com.example.moviefinder.exceptions.OmdbApiException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class OmdbClient {

    @Value("${omdb.api.key}")
    private String omdbApiKey;

    private final RestTemplate restTemplate;

    public OmdbClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public JSONObject fetchMovieData(String title) {
        try {
            String url = "http://www.omdbapi.com/?t=" + title + "&apikey=" + omdbApiKey;
            String response = restTemplate.getForObject(url, String.class);
            JSONObject json = new JSONObject(response);

            if (!json.optString("Response", "True").equalsIgnoreCase("True")) {
                throw new OmdbApiException("OMDb error: " + json.optString("Error", "Unknown error"));
            }

            return json;
        } catch (RestClientException e) {
            throw new OmdbApiException("Failed to fetch data from OMDb for title: " + title, e);
        }
    }
}