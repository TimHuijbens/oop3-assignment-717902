package com.example.moviefinder.controller;

import com.example.moviefinder.model.Movie;
import com.example.moviefinder.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller that handles HTTP requests for managing movies.
 * It provides endpoints to add, retrieve, update, and delete movie records.
 */
@RestController
@RequestMapping("/movies")
public class MovieController {

    @Autowired
    private MovieService movieService;

    /**
     * Adds a new movie by title using external APIs (OMDb and TMDb).
     *
     * @param title the title of the movie to add
     * @return the created {@link Movie} wrapped in a {@link ResponseEntity}
     */
    @PostMapping
    public ResponseEntity<Movie> addMovie(@RequestParam String title) {
        Movie movie = movieService.addMovieByTitle(title);
        return ResponseEntity.ok(movie);
    }

    /**
     * Retrieves a paginated list of all movies.
     *
     * @param page the page number (0-based)
     * @param size the number of items per page
     * @return a {@link Page} of {@link Movie} objects
     */
    @GetMapping
    public Page<Movie> getMovies(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size) {
        return movieService.getAllMovies(PageRequest.of(page, size));
    }

    /**
     * Retrieves a specific movie by its ID.
     *
     * @param id the ID of the movie
     * @return the {@link Movie} if found, or 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable Long id) {
        return movieService.getMovieById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Updates the watched status of a specific movie.
     *
     * @param id      the ID of the movie
     * @param watched whether the movie has been watched
     * @return the updated {@link Movie}, or 404 Not Found
     */
    @PostMapping("/{id}/watched")
    public ResponseEntity<Movie> updateWatched(@PathVariable Long id, @RequestParam boolean watched) {
        return movieService.updateWatched(id, watched)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Updates the user rating of a specific movie.
     *
     * @param id     the ID of the movie
     * @param rating the user rating (e.g., 1 to 5)
     * @return the updated {@link Movie}, or 404 Not Found
     */
    @PostMapping("/{id}/rating")
    public ResponseEntity<Movie> updateRating(@PathVariable Long id, @RequestParam int rating) {
        return movieService.updateRating(id, rating)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Deletes a specific movie by its ID.
     *
     * @param id the ID of the movie to delete
     */
    @DeleteMapping("/{id}")
    public void deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
    }
}
