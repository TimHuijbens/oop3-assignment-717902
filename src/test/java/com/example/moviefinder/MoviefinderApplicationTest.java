package com.example.moviefinder;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MoviefinderApplicationTest {

    @Test
    void contextLoads() {
        // If the context fails to load, this test will fail
    }

    @Test
    void mainMethod_ShouldStartApplication() {
        MoviefinderApplication.main(new String[]{});
    }
}
