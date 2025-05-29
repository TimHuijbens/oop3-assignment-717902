package com.example.moviefinder.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // OMDb fields
    private String title;
    @Column(name = "release_year")
    private String year;
    private String rated;
    private String released;
    private String runtime;
    private String genre;
    private String director;
    private String actors;
    private String plot;
    private String language;
    private String imdbRating;
    private String boxOffice;

    // TMDB data
    private String imagePath1;
    private String imagePath2;
    private String imagePath3;

    private String keywords;       // Comma-separated
    private String similarMovies;
    private String watchProviders;

    private boolean watched;
    private Integer rating; // 1 to 5
}