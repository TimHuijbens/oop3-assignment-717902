package com.example.moviefinder.util;

import com.example.moviefinder.model.Movie;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class MovieBuilder {

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

    private String parseActors(String actorString) {
        String[] actorsArray = actorString.split(",");
        return IntStream.range(0, Math.min(3, actorsArray.length))
                .mapToObj(i -> actorsArray[i].trim())
                .collect(Collectors.joining(", "));
    }

    private String extractKeywords(JSONObject keywords) {
        return flattenJsonArray(keywords.optJSONArray("keywords"), "name");
    }

    private String extractSimilarMovies(JSONObject similar) {
        return flattenJsonArray(similar.optJSONArray("results"), "title");
    }

    private String flattenJsonArray(JSONArray array, String key) {
        if (array == null) return "";
        return IntStream.range(0, Math.min(3, array.length()))
                .mapToObj(i -> array.getJSONObject(i).optString(key))
                .collect(Collectors.joining(", "));
    }

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