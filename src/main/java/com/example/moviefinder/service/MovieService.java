package com.example.moviefinder.service;

import com.example.moviefinder.model.Movie;
import com.example.moviefinder.repository.MovieRepository;
import com.example.moviefinder.util.MovieDataFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class for managing movie-related operations such as adding,
 * retrieving, updating, and deleting movies from the database.
 */
@Service
public class MovieService {

    @Autowired
    private MovieRepository repository;

    @Autowired
    private MovieDataFetcher movieDataFetcher;

    /**
     * Adds a new movie to the database using the provided title.
     * Fetches data from external APIs (OMDb, TMDb) and builds the Movie entity.
     *
     * @param title the title of the movie to fetch and add
     * @return the saved {@link Movie} entity
     * @throws com.example.moviefinder.exceptions.MovieDataFetchException
     *         if fetching movie data fails
     */
    public Movie addMovieByTitle(String title) {
        Movie movie = movieDataFetcher.fetchAndBuildMovie(title); // may throw custom runtime exceptions
        return repository.save(movie);
    }

    /**
     * Retrieves a movie by its ID.
     *
     * @param id the ID of the movie
     * @return an {@link Optional} containing the movie if found
     */
    public Optional<Movie> getMovieById(Long id) {
        return repository.findById(id);
    }

    /**
     * Retrieves all movies using pagination.
     *
     * @param pageable the pagination and sorting information
     * @return a paginated {@link Page} of movies
     */
    public Page<Movie> getAllMovies(Pageable pageable) {
        return repository.findAll(pageable);
    }

    /**
     * Updates the "watched" status of a movie.
     *
     * @param id the ID of the movie
     * @param watched the new watched status
     * @return an {@link Optional} containing the updated movie if found
     */
    public Optional<Movie> updateWatched(Long id, boolean watched) {
        return repository.findById(id).map(movie -> {
            movie.setWatched(watched);
            return repository.save(movie);
        });
    }

    /**
     * Updates the rating of a movie.
     *
     * @param id the ID of the movie
     * @param rating the new rating to assign
     * @return an {@link Optional} containing the updated movie if found
     */
    public Optional<Movie> updateRating(Long id, int rating) {
        return repository.findById(id).map(movie -> {
            movie.setRating(rating);
            return repository.save(movie);
        });
    }

    /**
     * Deletes a movie by its ID.
     *
     * @param id the ID of the movie to delete
     */
    public void deleteMovie(Long id) {
        repository.deleteById(id);
    }
}