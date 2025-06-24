package com.example.moviefinder.util;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class TmdbClient {

    @Value("${tmdb.api.key}")
    private String tmdbApiKey;

    private final RestTemplate restTemplate;

    public TmdbClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public JSONObject searchMovie(String title) throws Exception {
        String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);
        String url = "https://api.themoviedb.org/3/search/movie?api_key=" + tmdbApiKey + "&query=" + encodedTitle;
        String response = restTemplate.getForObject(new URI(url), String.class);
        return new JSONObject(response);
    }

    public JSONObject fetchMovieDetails(int movieId, String endpoint) throws Exception {
        String url = "https://api.themoviedb.org/3/movie/" + movieId + "/" + endpoint + "?api_key=" + tmdbApiKey;
        String response = restTemplate.getForObject(new URI(url), String.class);
        return new JSONObject(response);
    }
}