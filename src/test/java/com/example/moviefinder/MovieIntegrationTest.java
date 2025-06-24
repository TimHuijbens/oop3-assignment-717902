package com.example.moviefinder;

import com.example.moviefinder.model.Movie;
import org.junit.jupiter.api.Test;
import com.example.moviefinder.util.TmdbClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.json.JSONObject; // Add this import for JSON parsing

import static org.assertj.core.api.Assertions.assertThat;

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

    @Test
    void fullFlow_ShouldWorkCorrectly() {
        // Add a valid movie
        ResponseEntity<Movie> addResponse = restTemplate.postForEntity(getUrl("/movies?title=Inception"), null, Movie.class);
        assertThat(addResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Movie addedMovie = addResponse.getBody();
        assertThat(addedMovie).isNotNull();
        Long id = addedMovie.getId();

        // Get paginated movies
        ResponseEntity<String> getResponse = restTemplate.getForEntity(getUrl("/movies?page=0&size=5"), String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).contains("Inception");

        // Update watched
        ResponseEntity<Movie> watchedResponse = restTemplate.postForEntity(getUrl("/movies/" + id + "/watched?watched=true"), null, Movie.class);
        assertThat(watchedResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(watchedResponse.getBody().isWatched()).isTrue();

        // Update rating
        ResponseEntity<Movie> ratingResponse = restTemplate.postForEntity(getUrl("/movies/" + id + "/rating?rating=4"), null, Movie.class);
        assertThat(ratingResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(ratingResponse.getBody().getRating()).isEqualTo(4);

        // Delete movie
        restTemplate.delete(getUrl("/movies/" + id));

        // Simulate fetch after delete → expect NOT_FOUND
        ResponseEntity<Movie> afterDelete = restTemplate.postForEntity(getUrl("/movies/" + id + "/watched?watched=true"), null, Movie.class);
        assertThat(afterDelete.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void addMovie_InvalidTitle_ShouldReturnBadRequest() {
        String url = getUrl("/movies?title=");
        ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

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

    @Test
    void updateWatched_InvalidId_ShouldReturnNotFound() {
        ResponseEntity<Movie> response = restTemplate.postForEntity(getUrl("/movies/9999/watched?watched=true"), null, Movie.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void updateRating_InvalidId_ShouldReturnNotFound() {
        ResponseEntity<Movie> response = restTemplate.postForEntity(getUrl("/movies/9999/rating?rating=5"), null, Movie.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getMovieById_InvalidId_ShouldReturnNotFound() {
        ResponseEntity<Movie> response = restTemplate.postForEntity(getUrl("/movies/999999/watched?watched=false"), null, Movie.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}