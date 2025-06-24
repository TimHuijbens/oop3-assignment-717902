package com.example.moviefinder.util;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;

@Component
public class TmdbClient {

    @Value("${tmdb.api.key}")
    private String tmdbApiKey;

    public JSONObject searchMovie(String title) throws Exception {
        String query = "api_key=" + tmdbApiKey + "&query=" + URLEncoder.encode(title, "UTF-8");
        return new JSONObject(readFromUrl("https", "api.themoviedb.org", "/3/search/movie", query));
    }

    public JSONObject fetchMovieDetails(int movieId, String endpoint) throws Exception {
        String query = "api_key=" + tmdbApiKey;
        String path = "/3/movie/" + movieId + "/" + endpoint;
        return new JSONObject(readFromUrl("https", "api.themoviedb.org", path, query));
    }

    private String readFromUrl(String scheme, String host, String path, String query) throws Exception {
        URI uri = new URI(scheme, host, path, query, null);
        URL url = uri.toURL();

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }
}
