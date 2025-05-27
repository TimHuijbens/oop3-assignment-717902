package com.example.moviefinder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.json.*;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

@SpringBootApplication
public class MoviefinderApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(MoviefinderApplication.class, args);

        Scanner scanner = new Scanner(System.in);
        ExecutorService executor = Executors.newFixedThreadPool(3);

        try {
            // --- USER INPUT ---
            System.out.print("Enter movie title: ");
            String movieTitleInput = scanner.nextLine().trim();
            String omdbApiKey = "2208a53a"; // OMDB API key
            String tmdbApiKey = "0ab6a7c5544ba942107b83b9cef96b96"; // TMDB API key

            // --- PARALLEL API FETCHING ---
            Future<JSONObject> omdbFuture = executor.submit(() -> fetchOmdbData(movieTitleInput, omdbApiKey));
            Future<JSONObject> tmdbFuture = executor.submit(() -> fetchTmdbData(movieTitleInput, tmdbApiKey));

            JSONObject omdbData = omdbFuture.get();
            JSONObject tmdbData = tmdbFuture.get();

            if (omdbData == null || tmdbData.optString("Response").equals("False") || tmdbData == null) {
                System.out.println("Failed to retrieve movie data.");
                return;
            }

            // --- OMDb DATA EXTRACTION ---
            String title = omdbData.optString("Title");
            String year = omdbData.optString("Year");
            String rated = omdbData.optString("Rated");
            String released = omdbData.optString("Released");
            String runtime = omdbData.optString("Runtime");
            String genre = omdbData.optString("Genre");
            String director = omdbData.optString("Director");
            String plot = omdbData.optString("Plot");
            String language = omdbData.optString("Language");
            String imdbRating = omdbData.optString("imdbRating");
            String boxOffice = omdbData.optString("BoxOffice");

            // Actors: take first 3
            String[] actorsArray = omdbData.optString("Actors").split(",");
            String actors = String.join(", ",
                    Arrays.copyOfRange(actorsArray, 0, Math.min(3, actorsArray.length)));

            System.out.println("\n OMDb Movie Data:");
            System.out.println("Title: " + title);
            System.out.println("Year: " + year);
            System.out.println("Rated: " + rated);
            System.out.println("Released: " + released);
            System.out.println("Runtime: " + runtime);
            System.out.println("Genre: " + genre);
            System.out.println("Director: " + director);
            System.out.println("Actors: " + actors);
            System.out.println("Plot: " + plot);
            System.out.println("Language: " + language);
            System.out.println("IMDb Rating: " + imdbRating);
            System.out.println("Box Office: " + boxOffice);

            // --- TMDB: Get Movie ID from Search Results ---
            int tmdbMovieId = tmdbData.getJSONArray("results").getJSONObject(0).getInt("id");

            // --- FETCH ADDITIONAL TMDB DATA ---
            Future<JSONObject> imagesFuture = executor.submit(() -> fetchTmdbImages(tmdbMovieId, tmdbApiKey));
            Future<JSONObject> similarFuture = executor.submit(() -> fetchTmdbSimilar(tmdbMovieId, tmdbApiKey));
            Future<JSONObject> keywordsFuture = executor.submit(() -> fetchTmdbKeywords(tmdbMovieId, tmdbApiKey));
            Future<JSONObject> providersFuture = executor.submit(() -> fetchTmdbWatchProviders(tmdbMovieId, tmdbApiKey));

            JSONObject imageData = imagesFuture.get();
            JSONObject similarData = similarFuture.get();
            JSONObject keywordData = keywordsFuture.get();
            JSONObject providerData = providersFuture.get();

            // --- PRINT TMDB: SIMILAR MOVIES ---
            System.out.println("\n Similar Movies:");
            JSONArray similarResults = similarData.optJSONArray("results");
            for (int i = 0; i < Math.min(5, similarResults.length()); i++) {
                JSONObject movie = similarResults.getJSONObject(i);
                System.out.println("- " + movie.optString("title") + " (" + movie.optString("release_date") + ")");
            }

            // --- PRINT TMDB: KEYWORDS ---
            System.out.println("\n Keywords:");
            JSONArray keywords = keywordData.optJSONArray("keywords");
            for (int i = 0; i < keywords.length(); i++) {
                System.out.print(keywords.getJSONObject(i).optString("name"));
                if (i < keywords.length() - 1) System.out.print(", ");
            }
            System.out.println();

            // --- PRINT TMDB: WATCH PROVIDERS (US as example) ---
            System.out.println("\n Watch Providers (US):");
            JSONObject results = providerData.optJSONObject("results");
            if (results != null && results.has("US")) {
                JSONObject us = results.getJSONObject("US");
                JSONArray flatrate = us.optJSONArray("flatrate");
                if (flatrate != null) {
                    for (int i = 0; i < flatrate.length(); i++) {
                        System.out.println("- " + flatrate.getJSONObject(i).optString("provider_name"));
                    }
                } else {
                    System.out.println("No flatrate providers found.");
                }
            } else {
                System.out.println("No data for US region.");
            }

            // --- IMAGE DOWNLOAD TO FOLDER ---
            JSONArray backdrops = imageData.optJSONArray("backdrops");
            String safeTitle = title.replaceAll("[\\\\/:*?\"<>|]", "_");
            Files.createDirectories(Paths.get(safeTitle));

            List<Callable<Void>> downloadTasks = new ArrayList<>();
            for (int i = 0; i < Math.min(3, backdrops.length()); i++) {
                String filePath = backdrops.getJSONObject(i).getString("file_path");
                String fileName = filePath.replace("/", "_");
                String outputPath = safeTitle + "/" + fileName;

                downloadTasks.add(() -> {
                    downloadImage("https", "image.tmdb.org", "/t/p/w780" + filePath, outputPath);
                    return null;
                });
            }
            executor.invokeAll(downloadTasks);

            System.out.println("\n Images downloaded successfully into folder: " + safeTitle);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            executor.shutdown();
            scanner.close();
        }
    }

    // --- API FETCH HELPERS ---

    private static JSONObject fetchOmdbData(String title, String apiKey) throws Exception {
        String query = "t=" + URLEncoder.encode(title, "UTF-8") + "&apikey=" + apiKey;
        String json = readFromUrl("http", "www.omdbapi.com", "/", query);
        return new JSONObject(json);
    }

    private static JSONObject fetchTmdbData(String title, String apiKey) throws Exception {
        String query = "api_key=" + apiKey + "&query=" + URLEncoder.encode(title, "UTF-8");
        String json = readFromUrl("https", "api.themoviedb.org", "/3/search/movie", query);
        return new JSONObject(json);
    }

    private static JSONObject fetchTmdbImages(int id, String apiKey) throws Exception {
        String query = "api_key=" + apiKey;
        String json = readFromUrl("https", "api.themoviedb.org", "/3/movie/" + id + "/images", query);
        return new JSONObject(json);
    }

    private static JSONObject fetchTmdbSimilar(int id, String apiKey) throws Exception {
        String query = "api_key=" + apiKey;
        String json = readFromUrl("https", "api.themoviedb.org", "/3/movie/" + id + "/similar", query);
        return new JSONObject(json);
    }

    private static JSONObject fetchTmdbKeywords(int id, String apiKey) throws Exception {
        String query = "api_key=" + apiKey;
        String json = readFromUrl("https", "api.themoviedb.org", "/3/movie/" + id + "/keywords", query);
        return new JSONObject(json);
    }

    private static JSONObject fetchTmdbWatchProviders(int id, String apiKey) throws Exception {
        String query = "api_key=" + apiKey;
        String json = readFromUrl("https", "api.themoviedb.org", "/3/movie/" + id + "/watch/providers", query);
        return new JSONObject(json);
    }

    // --- GENERIC URL FETCHER ---
    private static String readFromUrl(String scheme, String host, String path, String query)
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

    // --- IMAGE DOWNLOAD (URI-based) ---
    private static void downloadImage(String scheme, String host, String path, String outputPath) {
        try {
            URI uri = new URI(scheme, host, path, null);
            URL url = uri.toURL();

            try (InputStream in = new BufferedInputStream(url.openStream());
                 OutputStream out = new FileOutputStream(outputPath)) {
                byte[] buffer = new byte[1024];
                int n;
                while ((n = in.read(buffer)) != -1) {
                    out.write(buffer, 0, n);
                }
            }
        } catch (Exception e) {
            System.out.println("Image download failed: " + e.getMessage());
        }
    }
}