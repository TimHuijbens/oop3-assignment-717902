package com.example.moviefinder.controller;

import com.example.moviefinder.model.Movie;
import com.example.moviefinder.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/movies")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @GetMapping
    public Page<Movie> getMovies(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size) {
        return movieService.getAllMovies(PageRequest.of(page, size));
    }

    @PatchMapping("/{id}/watched")
    public Optional<Movie> updateWatched(@PathVariable Long id,
                                         @RequestParam boolean watched) {
        return movieService.updateWatched(id, watched);
    }

    @PatchMapping("/{id}/rating")
    public Optional<Movie> updateRating(@PathVariable Long id,
                                        @RequestParam int rating) {
        return movieService.updateRating(id, rating);
    }

    @DeleteMapping("/{id}")
    public void deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
    }
}
