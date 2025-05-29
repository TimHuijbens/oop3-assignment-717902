package com.example.moviefinder;

import com.example.moviefinder.model.Movie;
import com.example.moviefinder.repository.MovieRepository;
import com.example.moviefinder.util.MovieDataFetcher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Scanner;

@SpringBootApplication
public class MoviefinderApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoviefinderApplication.class, args);
    }

    // Runs once at startup to fetch and save a movie based on terminal input
    @Bean
    public CommandLineRunner run(MovieDataFetcher movieDataFetcher, MovieRepository movieRepository) {
        return args -> {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter a movie title: ");
            String title = scanner.nextLine();

            try {
                Movie movie = movieDataFetcher.fetchAndBuildMovie(title);
                movieRepository.save(movie);
                System.out.println("✅ Movie saved to database: " + movie.getTitle());
            } catch (Exception e) {
                System.err.println("❌ Failed to fetch/save movie: " + e.getMessage());
            } finally {
                scanner.close();
            }
        };
    }
}