package com.example.moviefinder.util;

import com.example.moviefinder.model.Movie;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Utility class responsible for constructing a {@link Movie} entity by aggregating
 * and transforming data from OMDb and TMDb APIs.
 */
@Component
public class MovieBuilder {

    /**
     * Constructs a {@link Movie} object using information from OMDb and TMDb.
     *
     * @param omdbData        JSON object containing data from the OMDb API
     * @param tmdbSearchResult JSON object for the initial TMDb search result
     * @param images          JSON object containing TMDb image data
     * @param keywords        JSON object containing TMDb keywords
     * @param similar         JSON object containing TMDb similar movies
     * @param providers       JSON object containing TMDb watch providers
     * @param imagePaths      List of local paths where images were saved
     * @return A fully built {@link Movie} instance
     */
    public Movie buildMovie(JSONObject omdbData, JSONObject tmdbSearchResult, JSONObject images,
                            JSONObject keywords, JSONObject similar, JSONObject providers, List<String> imagePaths) {

        return Movie.builder()
                .title(omdbData.optString("Title"))
                .year(omdbData.optString("Year"))
                .rated(omdbData.optString("Rated"))
                .released(omdbData.optString("Released"))
                .runtime(omdbData.optString("Runtime"))
                .genre(omdbData.optString("Genre"))
                .director(omdbData.optString("Director"))
                .actors(parseActors(omdbData.optString("Actors")))
                .plot(omdbData.optString("Plot"))
                .language(omdbData.optString("Language"))
                .imdbRating(omdbData.optString("imdbRating"))
                .boxOffice(omdbData.optString("BoxOffice"))
                .imagePath1(imagePaths.size() > 0 ? imagePaths.get(0) : null)
                .imagePath2(imagePaths.size() > 1 ? imagePaths.get(1) : null)
                .imagePath3(imagePaths.size() > 2 ? imagePaths.get(2) : null)
                .keywords(extractKeywords(keywords))
                .similarMovies(extractSimilarMovies(similar))
                .watchProviders(extractWatchProviders(providers))
                .watched(false)
                .rating(null)
                .build();
    }

    /**
     * Extracts up to 3 actor names from the comma-separated actor string.
     *
     * @param actorString the string of actors from OMDb
     * @return formatted string of up to 3 actors
     */
    private String parseActors(String actorString) {
        String[] actorsArray = actorString.split(",");
        return IntStream.range(0, Math.min(3, actorsArray.length))
                .mapToObj(i -> actorsArray[i].trim())
                .collect(Collectors.joining(", "));
    }

    /**
     * Extracts keywords from the TMDb JSON object.
     *
     * @param keywords JSON object containing the "keywords" array
     * @return comma-separated list of keyword names
     */
    private String extractKeywords(JSONObject keywords) {
        return flattenJsonArray(keywords.optJSONArray("keywords"), "name");
    }

    /**
     * Extracts similar movie titles from the TMDb JSON object.
     *
     * @param similar JSON object containing the "results" array
     * @return comma-separated list of similar movie titles
     */
    private String extractSimilarMovies(JSONObject similar) {
        return flattenJsonArray(similar.optJSONArray("results"), "title");
    }

    /**
     * Converts a JSON array into a comma-separated string using the given key.
     *
     * @param array the JSONArray to flatten
     * @param key   the key to extract values from each object
     * @return comma-separated string of extracted values
     */
    private String flattenJsonArray(JSONArray array, String key) {
        if (array == null) return "";
        return IntStream.range(0, Math.min(3, array.length()))
                .mapToObj(i -> array.getJSONObject(i).optString(key))
                .collect(Collectors.joining(", "));
    }

    /**
     * Extracts watch provider names from the TMDb JSON object.
     * Focuses on "US" region and "flatrate" category.
     *
     * @param providerData JSON object containing watch provider info
     * @return comma-separated list of provider names
     */
    private String extractWatchProviders(JSONObject providerData) {
        JSONObject results = providerData.optJSONObject("results");
        if (results != null && results.has("US")) {
            JSONArray flatrate = results.getJSONObject("US").optJSONArray("flatrate");
            if (flatrate != null) {
                return IntStream.range(0, flatrate.length())
                        .mapToObj(i -> flatrate.getJSONObject(i).optString("provider_name"))
                        .collect(Collectors.joining(", "));
            }
        }
        return "";
    }
}