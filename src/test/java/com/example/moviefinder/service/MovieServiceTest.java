package com.example.moviefinder.service;

import com.example.moviefinder.model.Movie;
import com.example.moviefinder.repository.MovieRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieService movieService;

    @Test
    void updateWatched_ShouldUpdateFlag() {
        // Arrange
        Movie movie = new Movie();
        movie.setId(1L);
        movie.setWatched(false);

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(movieRepository.save(any(Movie.class))).thenReturn(movie);

        // Act
        Optional<Movie> result = movieService.updateWatched(1L, true);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().isWatched()).isTrue();
    }

    @Test
    void updateRating_ShouldUpdateRating() {
        // Arrange
        Movie movie = new Movie();
        movie.setId(2L);
        movie.setRating(3);

        when(movieRepository.findById(2L)).thenReturn(Optional.of(movie));
        when(movieRepository.save(any(Movie.class))).thenReturn(movie);

        // Act
        Optional<Movie> result = movieService.updateRating(2L, 5);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getRating()).isEqualTo(5);
    }
}