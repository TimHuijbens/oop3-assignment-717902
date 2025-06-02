package com.example.moviefinder.util;

import com.example.moviefinder.model.Movie;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

@Component
public class MovieDataFetcher {

    @Value("${omdb.api.key}")
    private String omdbApiKey; // OMDB Key safely put in the application.properties

    @Value("${tmdb.api.key}")
    private String tmdbApiKey; // TMDB Key safely put in the application.properties

    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    public Movie fetchAndBuildMovie(String titleInput) throws Exception {
        Future<JSONObject> omdbFuture = executor.submit(() -> fetchOmdbData(titleInput)); // Fetching OMDB data based on the title 
        Future<JSONObject> tmdbFuture = executor.submit(() -> fetchTmdbSearch(titleInput)); // Fetching TMDB data based on the title

        JSONObject omdbData = omdbFuture.get();
        JSONObject tmdbSearchData = tmdbFuture.get();

        if (omdbData == null || tmdbSearchData == null || tmdbSearchData.getJSONArray("results").isEmpty()) {
            throw new IllegalStateException("Failed to fetch movie data."); // Throwing Exception when no data can be found for the given title
        }

        JSONObject tmdbResult = tmdbSearchData.getJSONArray("results").getJSONObject(0);
        
        // Fetching relevant data from the TMDB API
        int tmdbId = tmdbResult.getInt("id");

        Future<JSONObject> imagesFuture = executor.submit(() -> fetchTmdbData(tmdbId, "images"));
        Future<JSONObject> keywordsFuture = executor.submit(() -> fetchTmdbData(tmdbId, "keywords"));
        Future<JSONObject> similarFuture = executor.submit(() -> fetchTmdbData(tmdbId, "similar"));
        Future<JSONObject> providersFuture = executor.submit(() -> fetchTmdbData(tmdbId, "watch/providers"));

        JSONObject images = imagesFuture.get();
        JSONObject keywords = keywordsFuture.get();
        JSONObject similar = similarFuture.get();
        JSONObject providers = providersFuture.get();
        
        // Fetching relevant data from the OMDB API
        List<String> imagePaths = downloadImages(images, omdbData.getString("Title"));

        // Extract first 3 actors
        String[] actorsArray = omdbData.optString("Actors", "").split(",");
        String actors = String.join(", ",
                Arrays.copyOfRange(actorsArray, 0, Math.min(3, actorsArray.length)));
                
        // Build a movie that can be added to the database        
        return Movie.builder() // 
                .title(omdbData.optString("Title"))
                .year(omdbData.optString("Year"))
                .rated(omdbData.optString("Rated"))
                .released(omdbData.optString("Released"))
                .runtime(omdbData.optString("Runtime"))
                .genre(omdbData.optString("Genre"))
                .director(omdbData.optString("Director"))
                .actors(actors)
                .plot(omdbData.optString("Plot"))
                .language(omdbData.optString("Language"))
                .imdbRating(omdbData.optString("imdbRating"))
                .boxOffice(omdbData.optString("BoxOffice"))

                .imagePath1(imagePaths.size() > 0 ? imagePaths.get(0) : null)
                .imagePath2(imagePaths.size() > 1 ? imagePaths.get(1) : null)
                .imagePath3(imagePaths.size() > 2 ? imagePaths.get(2) : null)

                .keywords(flattenArray(keywords.optJSONArray("keywords"), "name"))
                .similarMovies(flattenArray(similar.optJSONArray("results"), "title"))
                .watchProviders(extractWatchProviders(providers))

                .watched(false)
                .rating(null)
                .build();
    }

    private JSONObject fetchOmdbData(String title) throws Exception { // OMDB fetching code
        String query = "t=" + URLEncoder.encode(title, "UTF-8") + "&apikey=" + omdbApiKey;
        String json = readFromUrl("http", "www.omdbapi.com", "/", query);
        return new JSONObject(json);
    }

    private JSONObject fetchTmdbSearch(String title) throws Exception { // TMDB search code
        String query = "api_key=" + tmdbApiKey + "&query=" + URLEncoder.encode(title, "UTF-8");
        String json = readFromUrl("https", "api.themoviedb.org", "/3/search/movie", query);
        return new JSONObject(json);
    }

    private JSONObject fetchTmdbData(int movieId, String endpoint) throws Exception { // TMDB fetching code
        String query = "api_key=" + tmdbApiKey;
        String path = "/3/movie/" + movieId + "/" + endpoint;
        String json = readFromUrl("https", "api.themoviedb.org", path, query);
        return new JSONObject(json);
    }

    private String readFromUrl(String scheme, String host, String path, String query)
            throws IOException, URISyntaxException {
        URI uri = new URI(scheme, host, path, query, null);
        URL url = uri.toURL();

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            String line;
            while ((line = reader.readLine()) != null)
                sb.append(line);
        }
        return sb.toString();
    }

    // Code for downloading three images and downloading them to a local file
    private List<String> downloadImages(JSONObject imageData, String title) {
        List<String> paths = new ArrayList<>();
        try {
            JSONArray backdrops = imageData.optJSONArray("backdrops");
            if (backdrops == null || backdrops.isEmpty()) return paths;

            String safeTitle = title.replaceAll("[\\\\/:*?\"<>|]", "_");
            Path folderPath = Paths.get(safeTitle);
            Files.createDirectories(folderPath);

            for (int i = 0; i < Math.min(3, backdrops.length()); i++) {
                String filePath = backdrops.getJSONObject(i).optString("file_path");
                String outputName = filePath.replace("/", "_");
                Path outputPath = folderPath.resolve(outputName);

                URI uri = new URI("https", "image.tmdb.org", "/t/p/w780" + filePath, null);
                URL url = uri.toURL();

                try (InputStream in = new BufferedInputStream(url.openStream());
                     OutputStream out = new FileOutputStream(outputPath.toFile())) {
                    byte[] buffer = new byte[1024];
                    int n;
                    while ((n = in.read(buffer)) != -1) {
                        out.write(buffer, 0, n);
                    }
                    paths.add(outputPath.toString());
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to download image: " + e.getMessage());
        }
        return paths;
    }

    private String flattenArray(JSONArray array, String key) {
        if (array == null) return "";
        List<String> values = new ArrayList<>();
        for (int i = 0; i < Math.min(3, array.length()); i++) {  // Only first 3 entries
            values.add(array.getJSONObject(i).optString(key));
        }
        return String.join(", ", values);
    }

    // extracting the watch providers for movies in the US
    private String extractWatchProviders(JSONObject providerData) {
        JSONObject results = providerData.optJSONObject("results");
        if (results != null && results.has("US")) {
            JSONObject us = results.getJSONObject("US");
            JSONArray flatrate = us.optJSONArray("flatrate");
            if (flatrate != null) {
                List<String> names = new ArrayList<>();
                for (int i = 0; i < flatrate.length(); i++) {
                    names.add(flatrate.getJSONObject(i).optString("provider_name"));
                }
                return String.join(", ", names);
            }
        }
        return "";
    }
}