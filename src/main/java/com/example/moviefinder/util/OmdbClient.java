package com.example.moviefinder.util;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OmdbClient {

    @Value("${omdb.api.key}")
    private String omdbApiKey;

    private final RestTemplate restTemplate;

    public OmdbClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public JSONObject fetchMovieData(String title) throws Exception {
        String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);
        String url = "http://www.omdbapi.com/?t=" + encodedTitle + "&apikey=" + omdbApiKey;
        String response = restTemplate.getForObject(url, String.class);
        return new JSONObject(response);
    }
}