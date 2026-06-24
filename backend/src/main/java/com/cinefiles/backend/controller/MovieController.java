package com.cinefiles.backend.controller;

import com.cinefiles.backend.entity.Movie;
import com.cinefiles.backend.service.MovieService;
import com.cinefiles.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/movies")
public class MovieController {

    // 1. Inject the Brains!
    @Autowired
    private MovieService movieService;

    @Autowired
    private UserService userService;

    // --- THE SEARCH ENDPOINT ---
    @GetMapping("/search")
    @RateLimiter(name = "movieSearch", fallbackMethod = "rateLimiterFallback")
    public ResponseEntity<?> searchForMovie(@RequestParam String title) {
        // Now using the Spring Boot Service instead of the dead ApiManager
        Movie movieData = movieService.fetchAndCacheMovie(title);

        if (movieData == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Movie not found on OMDb."));
        }
        return ResponseEntity.ok(movieData);
    }

    // THE FALLBACK
    public ResponseEntity<?> rateLimiterFallback(String title, Exception ex) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(Map.of("error", "🚨 Bouncer Alert: Too many requests! 5 searches per 10 seconds limit. Slow down."));
    }

    // --- THE RECOMMENDATION ENDPOINT ---
    @GetMapping("/recommendations")
    public ResponseEntity<?> getRecommendations(@RequestParam String title) {
        // Uses the new MovieService to fetch the custom native SQL query
        List<Movie> recommendations = movieService.getRecommendations(title);
        return ResponseEntity.ok(recommendations);
    }

    // --- THE WATCHLIST ENDPOINT ---
    @PostMapping("/watchlist/add")
    public ResponseEntity<?> addToWatchlist(@RequestParam String username, @RequestParam String title) {
        try {
            // Uses the new UserService to handle the JPA bridge table mapping
            userService.addMovieToWatchlist(username, title);
            return ResponseEntity.ok(Map.of("message", "Success: " + title + " has been added to " + username + "'s watchlist!"));
        } catch (Exception e) {
            // Catches duplicates or missing users safely
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
