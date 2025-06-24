package com.example.moviefinder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Entry point for the MovieFinder Spring Boot application.
 * <p>
 * This class bootstraps the application and registers any required global beans,
 * such as the {@link RestTemplate} used for external API calls.
 */
@SpringBootApplication
public class MoviefinderApplication {

    /**
     * Main method that launches the Spring Boot application.
     *
     * @param args application startup arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(MoviefinderApplication.class, args);
        System.out.println("✅ REST API is running at: http://localhost:8080/movies");
    }

    /**
     * Registers a {@link RestTemplate} bean in the Spring application context.
     * Used for performing HTTP requests to external services like OMDb and TMDb.
     *
     * @return a singleton {@code RestTemplate} instance
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}