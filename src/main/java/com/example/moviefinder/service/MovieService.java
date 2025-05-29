package com.example.moviefinder.service;

import com.example.moviefinder.model.Movie;
import com.example.moviefinder.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MovieService {

    @Autowired
    private MovieRepository repository;

    public Page<Movie> getAllMovies(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Optional<Movie> updateWatched(Long id, boolean watched) {
        return repository.findById(id).map(movie -> {
            movie.setWatched(watched);
            return repository.save(movie);
        });
    }

    public Optional<Movie> updateRating(Long id, int rating) {
        return repository.findById(id).map(movie -> {
            movie.setRating(rating);
            return repository.save(movie);
        });
    }

    public void deleteMovie(Long id) {
        repository.deleteById(id);
    }
}