package com.example.moviefinder.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing a movie with metadata from both OMDb and TMDb APIs.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {

    /**
     * Primary key identifier for the movie.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // === OMDb fields ===

    /**
     * Title of the movie.
     */
    private String title;

    /**
     * Release year of the movie.
     */
    @Column(name = "release_year")
    private String year;

    /**
     * Age rating (e.g., PG-13, R).
     */
    private String rated;

    /**
     * Official release date.
     */
    private String released;

    /**
     * Runtime duration (e.g., "148 min").
     */
    private String runtime;

    /**
     * Genre(s) of the movie (e.g., "Action, Sci-Fi").
     */
    private String genre;

    /**
     * Director(s) of the movie.
     */
    private String director;

    /**
     * Main actors or cast members.
     */
    private String actors;

    /**
     * Short plot summary.
     */
    private String plot;

    /**
     * Language(s) of the movie.
     */
    private String language;

    /**
     * IMDb rating (e.g., "8.8").
     */
    private String imdbRating;

    /**
     * Box office earnings (e.g., "$829,895,144").
     */
    private String boxOffice;

    // === TMDb data ===

    /**
     * First image path retrieved from TMDb.
     */
    private String imagePath1;

    /**
     * Second image path retrieved from TMDb.
     */
    private String imagePath2;

    /**
     * Third image path retrieved from TMDb.
     */
    private String imagePath3;

    /**
     * Keywords associated with the movie (comma-separated).
     */
    private String keywords;

    /**
     * List of similar movies from TMDb (comma-separated).
     */
    private String similarMovies;

    /**
     * Streaming/watch providers (comma-separated).
     */
    private String watchProviders;

    /**
     * Whether the movie has been marked as watched by the user.
     */
    private boolean watched;

    /**
     * User's rating of the movie (1 to 5 scale).
     */
    private Integer rating;
}