package com.example.moviefinder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MoviefinderApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoviefinderApplication.class, args);
        System.out.println("REST API is running at: http://localhost:8080/movies");
        System.out.println("Use a REST client (e.g., Postman, curl, browser) to interact with it.");
    }
}