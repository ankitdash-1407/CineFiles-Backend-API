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
    // THE UPGRADED FALLBACK
    public ResponseEntity<?> rateLimiterFallback(String title, Exception ex) {
        if (ex instanceof io.github.resilience4j.ratelimiter.RequestNotPermitted) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("error", "🚨 Bouncer Alert: Too many requests! 5 searches per 10 seconds limit. Slow down."));
        }

        // This will print the REAL reason OMDb is failing to your IntelliJ console
        System.out.println("🚨 REAL ERROR CAUGHT IN FALLBACK: " + ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Server crashed: " + ex.getMessage()));
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

    // --- GET WATCHLIST ENDPOINT ---
    @GetMapping("/watchlist")
    public ResponseEntity<?> getWatchlist(@RequestParam String username) {
        try {
            // Calls the UserService to pull the user's saved movies
            List<Movie> watchlist = userService.getWatchlist(username);
            return ResponseEntity.ok(watchlist);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch watchlist: " + e.getMessage()));
        }
    }
}
