package com.example.moviefinder.util;

import com.example.moviefinder.exceptions.MovieDataFetchException;
import com.example.moviefinder.exceptions.TmdbApiException;
import com.example.moviefinder.model.Movie;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.*;

/**
 * A utility component responsible for orchestrating the retrieval of movie data
 * from OMDb and TMDb APIs, aggregating it, and constructing a {@link Movie} entity.
 *
 * <p>Fetches primary movie data sequentially, then performs parallel requests for
 * additional data like images, keywords, similar movies, and watch providers.</p>
 */
@Component
public class MovieDataFetcher {

    private final OmdbClient omdbClient;
    private final TmdbClient tmdbClient;
    private final ImageDownloader imageDownloader;
    private final MovieBuilder movieBuilder;
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    /**
     * Constructs the MovieDataFetcher with all required dependencies.
     *
     * @param omdbClient      client to fetch data from OMDb API
     * @param tmdbClient      client to fetch data from TMDb API
     * @param imageDownloader utility for downloading image files
     * @param movieBuilder    utility to build a {@link Movie} from collected data
     */
    public MovieDataFetcher(OmdbClient omdbClient, TmdbClient tmdbClient,
                            ImageDownloader imageDownloader, MovieBuilder movieBuilder) {
        this.omdbClient = omdbClient;
        this.tmdbClient = tmdbClient;
        this.imageDownloader = imageDownloader;
        this.movieBuilder = movieBuilder;
    }

    /**
     * Fetches movie data from OMDb and TMDb, performs additional resource lookups,
     * downloads images, and builds a {@link Movie} entity.
     *
     * @param titleInput the movie title to search
     * @return a fully populated {@link Movie} object
     * @throws MovieDataFetchException if any fetching or processing error occurs
     * @throws TmdbApiException        if no TMDb results are found
     */
    public Movie fetchAndBuildMovie(String titleInput) {
        try {
            // Step 1: Fetch data from OMDb and TMDb (basic search)
            JSONObject omdbData = omdbClient.fetchMovieData(titleInput); // May throw OmdbApiException
            JSONObject tmdbSearch = tmdbClient.searchMovie(titleInput);  // May throw TmdbApiException

            if (tmdbSearch == null || tmdbSearch.getJSONArray("results").isEmpty()) {
                throw new TmdbApiException("No TMDB results found for title: " + titleInput);
            }

            JSONObject tmdbResult = tmdbSearch.getJSONArray("results").getJSONObject(0);
            int tmdbId = tmdbResult.getInt("id");

            // Step 2: Fetch additional TMDb resources in parallel
            Future<JSONObject> imagesFuture = executor.submit(() -> tmdbClient.fetchMovieDetails(tmdbId, "images"));
            Future<JSONObject> keywordsFuture = executor.submit(() -> tmdbClient.fetchMovieDetails(tmdbId, "keywords"));
            Future<JSONObject> similarFuture = executor.submit(() -> tmdbClient.fetchMovieDetails(tmdbId, "similar"));
            Future<JSONObject> providersFuture = executor.submit(() -> tmdbClient.fetchMovieDetails(tmdbId, "watch/providers"));

            JSONObject images = imagesFuture.get();
            JSONObject keywords = keywordsFuture.get();
            JSONObject similar = similarFuture.get();
            JSONObject providers = providersFuture.get();

            // Step 3: Download image files and build Movie entity
            List<String> imagePaths = imageDownloader.downloadImages(images, omdbData.getString("Title"));
            return movieBuilder.buildMovie(omdbData, tmdbResult, images, keywords, similar, providers, imagePaths);

        } catch (ExecutionException e) {
            // Propagate underlying exception from any failed future
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            throw new MovieDataFetchException("Error during parallel data fetching", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Best practice to reset the thread interrupt flag
            throw new MovieDataFetchException("Thread interrupted during data fetch", e);
        }
    }
}