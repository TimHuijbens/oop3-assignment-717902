package com.example.moviefinder.repository;

import com.example.moviefinder.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {}