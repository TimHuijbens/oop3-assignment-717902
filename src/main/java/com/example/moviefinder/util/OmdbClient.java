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
public class OmdbClient {

    @Value("${omdb.api.key}")
    private String omdbApiKey;

    public JSONObject fetchMovieData(String title) throws Exception {
        String query = "t=" + URLEncoder.encode(title, "UTF-8") + "&apikey=" + omdbApiKey;
        String json = readFromUrl("http", "www.omdbapi.com", "/", query);
        return new JSONObject(json);
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