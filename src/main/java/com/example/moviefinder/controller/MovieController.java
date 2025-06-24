package com.example.moviefinder.controller;

import com.example.moviefinder.model.Movie;
import com.example.moviefinder.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/movies")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @PostMapping
        public ResponseEntity<Movie> addMovie(@RequestParam String title) {
            Movie movie = movieService.addMovieByTitle(title);
            return ResponseEntity.ok(movie);
        }

    @GetMapping
    public Page<Movie> getMovies(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size) {
        return movieService.getAllMovies(PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable Long id) {
        return movieService.getMovieById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/watched")
    public ResponseEntity<Movie> updateWatched(@PathVariable Long id, @RequestParam boolean watched) {
        return movieService.updateWatched(id, watched)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/rating")
    public ResponseEntity<Movie> updateRating(@PathVariable Long id, @RequestParam int rating) {
        return movieService.updateRating(id, rating)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public void deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
    }
}
