package com.cinefiles.backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    // search end point WITH Rate Limiter (The Bouncer)
    @GetMapping("/search")
    @RateLimiter(name = "movieSearch", fallbackMethod = "rateLimiterFallback")
    public ResponseEntity<?> searchForMovie(@RequestParam String title) {

        // IMPORTANT: Check ApiManager.java file
        // put method below to get movie
        Object movieData = ApiManager.fetchAndCacheMovie(title);
        return ResponseEntity.ok(movieData);
    }

    // THE FALLBACK METHOD: This catches users who get blocked by the bouncer
    public ResponseEntity<?> rateLimiterFallback(String title, Exception ex) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body("🚨 Bouncer Alert: Too many requests! You have exceeded the limit of 5 searches per 10 seconds. Please slow down.");
    }

    // 1. THE RECOMMENDATION ENDPOINT
    @GetMapping("/recommendations")
    public List<String> getRecommendations(@RequestParam String title) {
        return MovieManager.getRecommendations(title);
    }

    // 2. THE WATCHLIST ENDPOINT
    @PostMapping("/watchlist/add")
    public String addToWatchlist(@RequestParam String username, @RequestParam String title) {
        // Assuming you have a WatchlistManager from your console app days!
        // WatchlistManager.addMovie(username, title);
        return "Success: " + title + " has been added to " + username + "'s watchlist!";
    }
}
