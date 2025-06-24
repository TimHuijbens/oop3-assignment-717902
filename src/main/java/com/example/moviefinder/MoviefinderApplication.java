package com.example.moviefinder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class MoviefinderApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoviefinderApplication.class, args);
        System.out.println("✅ REST API is running at: http://localhost:8080/movies");
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}