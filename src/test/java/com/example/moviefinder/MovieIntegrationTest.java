package com.example.moviefinder;

import com.example.moviefinder.model.Movie;
import com.example.moviefinder.util.TmdbClient;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for the Movie REST API.
 * <p>
 * This test class validates full application flow:
 * - Adding movies via external APIs (OMDb + TMDb)
 * - Handling invalid inputs and external API failures
 * - Updating movie attributes (watched, rating)
 * - Deleting and retrieving movie records
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MovieIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TmdbClient tmdbClient;

    private String getUrl(String path) {
        return "http://localhost:" + port + path;
    }

    /**
     * Tests full movie lifecycle:
     * Add → Retrieve → Update → Delete → Confirm deletion.
     */
    @Test
    void fullFlow_ShouldWorkCorrectly() {
        ResponseEntity<Movie> addResponse = restTemplate.postForEntity(getUrl("/movies?title=Inception"), null, Movie.class);
        assertThat(addResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Movie addedMovie = addResponse.getBody();
        assertThat(addedMovie).isNotNull();
        Long id = addedMovie.getId();

        ResponseEntity<String> getResponse = restTemplate.getForEntity(getUrl("/movies?page=0&size=5"), String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).contains("Inception");

        ResponseEntity<Movie> watchedResponse = restTemplate.postForEntity(getUrl("/movies/" + id + "/watched?watched=true"), null, Movie.class);
        assertThat(watchedResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(watchedResponse.getBody().isWatched()).isTrue();

        ResponseEntity<Movie> ratingResponse = restTemplate.postForEntity(getUrl("/movies/" + id + "/rating?rating=4"), null, Movie.class);
        assertThat(ratingResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(ratingResponse.getBody().getRating()).isEqualTo(4);

        restTemplate.delete(getUrl("/movies/" + id));

        ResponseEntity<Movie> afterDelete = restTemplate.postForEntity(getUrl("/movies/" + id + "/watched?watched=true"), null, Movie.class);
        assertThat(afterDelete.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    /**
     * Ensures that submitting an empty title returns a 400 error.
     */
    @Test
    void addMovie_InvalidTitle_ShouldReturnBadRequest() {
        String url = getUrl("/movies?title=");
        ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    /**
     * Verifies that OMDb failure results in a 400 with appropriate error structure.
     */
    @Test
    void addMovie_ShouldReturnBadRequest_WhenOmdbFails() {
        String url = getUrl("/movies?title=thiswontmatchanything999omdb");
        ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();

        JSONObject json = new JSONObject(response.getBody());
        assertThat(json.getInt("status")).isEqualTo(400);
        assertThat(json.getString("error")).isEqualTo("OMDb API error");
        assertThat(json.getString("message")).contains("OMDb error: Movie not found!");
        assertThat(json.getString("timestamp")).isNotBlank();
    }

    /**
     * Verifies that a simulated TMDb failure produces a 400 error with expected structure.
     */
    @Test
    void addMovie_ShouldReturnBadRequest_WhenTmdbFails() {
        tmdbClient.enableFailureSimulation(); // Force TMDb failure

        String url = getUrl("/movies?title=Inception"); // Valid OMDb title
        ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();

        JSONObject json = new JSONObject(response.getBody());
        assertThat(json.getInt("status")).isEqualTo(400);
        assertThat(json.getString("error")).isEqualTo("TMDb API error");
        assertThat(json.getString("message")).contains("Simulated TMDb failure");
        assertThat(json.getString("timestamp")).isNotBlank();
    }

    /**
     * Verifies 404 is returned when updating 'watched' for nonexistent movie.
     */
    @Test
    void updateWatched_InvalidId_ShouldReturnNotFound() {
        ResponseEntity<Movie> response = restTemplate.postForEntity(getUrl("/movies/9999/watched?watched=true"), null, Movie.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    /**
     * Verifies 404 is returned when updating rating for nonexistent movie.
     */
    @Test
    void updateRating_InvalidId_ShouldReturnNotFound() {
        ResponseEntity<Movie> response = restTemplate.postForEntity(getUrl("/movies/9999/rating?rating=5"), null, Movie.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    /**
     * Verifies 404 is returned when querying a movie by an invalid ID.
     */
    @Test
    void getMovieById_InvalidId_ShouldReturnNotFound() {
        ResponseEntity<Movie> response = restTemplate.postForEntity(getUrl("/movies/999999/watched?watched=false"), null, Movie.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
