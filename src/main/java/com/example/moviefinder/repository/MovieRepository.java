package com.example.moviefinder.repository;

import com.example.moviefinder.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for {@link Movie} entities.
 * <p>
 * Extends {@link JpaRepository} to provide CRUD operations,
 * pagination, and query method execution for Movie entities.
 */
public interface MovieRepository extends JpaRepository<Movie, Long> {
    // No additional methods required for now; JpaRepository provides all basic operations.
}