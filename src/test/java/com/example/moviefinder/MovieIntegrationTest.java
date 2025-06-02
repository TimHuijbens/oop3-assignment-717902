package com.example.moviefinder;

import com.example.moviefinder.model.Movie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class MovieIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getUrl(String path) {
        return "http://localhost:" + port + path;
    }

    @Test
    void addMovie_ShouldCreateMovie() {
        String url = getUrl("/movies?title=The Matrix");

        ResponseEntity<Movie> response = restTemplate.postForEntity(url, null, Movie.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualToIgnoringCase("The Matrix");
    }

    @Test
    void getMovies_ShouldReturnPaginatedList() {
        ResponseEntity<String> response = restTemplate.getForEntity(getUrl("/movies?page=0&size=5"), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("title");
    }

    @Test
    void updateWatched_ShouldUpdateFlag() {
        // Add movie first
        ResponseEntity<Movie> addResp = restTemplate.postForEntity(getUrl("/movies?title=Inception"), null, Movie.class);
        Long id = addResp.getBody().getId();

        String url = getUrl("/movies/" + id + "/watched?watched=true");
        ResponseEntity<Movie> updateResp = restTemplate.postForEntity(url, null, Movie.class);

        assertThat(updateResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResp.getBody().isWatched()).isTrue();
    }

    @Test
    void updateRating_ShouldUpdateRating() {
        // Add movie first
        ResponseEntity<Movie> addResp = restTemplate.postForEntity(getUrl("/movies?title=Fight Club"), null, Movie.class);
        Long id = addResp.getBody().getId();

        String url = getUrl("/movies/" + id + "/rating?rating=4");
        ResponseEntity<Movie> updateResp = restTemplate.postForEntity(url, null, Movie.class);

        assertThat(updateResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResp.getBody().getRating()).isEqualTo(4);
    }

    @Test
    void deleteMovie_ShouldRemoveMovie() {
        // Add movie first
        ResponseEntity<Movie> addResp = restTemplate.postForEntity(getUrl("/movies?title=The Shawshank Redemption"), null, Movie.class);
        Long id = addResp.getBody().getId();

        restTemplate.delete(getUrl("/movies/" + id));
    }
}