package com.example.moviefinder.service;

import com.example.moviefinder.model.Movie;
import com.example.moviefinder.repository.MovieRepository;
import com.example.moviefinder.util.MovieDataFetcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MovieService {

    @Autowired
    private MovieRepository repository;

    @Autowired
    private MovieDataFetcher movieDataFetcher;

    // Adding a movie based on the title using the fetchAndBuild function
    public Movie addMovieByTitle(String title) throws Exception {
        Movie movie = movieDataFetcher.fetchAndBuildMovie(title);
        return repository.save(movie);
    }

    // Getting all movies in the database (repositories)
    public Page<Movie> getAllMovies(Pageable pageable) {
        return repository.findAll(pageable);
    }

    // Updating if a movie is watched or not
    public Optional<Movie> updateWatched(Long id, boolean watched) {
        return repository.findById(id).map(movie -> {
            movie.setWatched(watched);
            return repository.save(movie);
        });
    }

    // Update the rating based on the movie experience
    public Optional<Movie> updateRating(Long id, int rating) {
        return repository.findById(id).map(movie -> {
            movie.setRating(rating);
            return repository.save(movie);
        });
    }

    // Delete the movie out of the Database
    public void deleteMovie(Long id) {
        repository.deleteById(id);
    }
}