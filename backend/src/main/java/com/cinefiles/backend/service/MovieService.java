package com.cinefiles.backend.service;

import com.cinefiles.backend.entity.Movie;
import com.cinefiles.backend.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; // Added this import
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    // Pulls directly from application.properties now
    @Value("${omdb.api.key}")
    private String apiKey;

    public Movie fetchAndCacheMovie(String title) {
        // --- 1. THE CACHE CHECK ---
        Movie existingMovie = movieRepository.findByTitle(title);

        if (existingMovie != null) {
            System.out.println("[⚡ CACHE HIT] '" + title + "' found in Vault!");
            return existingMovie;
        }

        System.out.println("[🌐 CACHE MISS] Connecting to OMDb...");
        String formattedTitle = title.replace(" ", "+");
        String url = "http://www.omdbapi.com/?t=" + formattedTitle + "&apikey=" + apiKey;

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String jsonResponse = response.body();

            if (jsonResponse.contains("\"Response\":\"False\"")) {
                return null;
            }

            // --- THE FIX: Extracting the ID ---
            String fetchedImdbId = extractJsonValue(jsonResponse, "imdbID"); // Added this
            String fetchedTitle = extractJsonValue(jsonResponse, "Title");
            String fetchedGenre = extractJsonValue(jsonResponse, "Genre");
            String imdbRatingStr = extractJsonValue(jsonResponse, "imdbRating");
            double rating = imdbRatingStr.equals("N/A") ? 0.0 : Double.parseDouble(imdbRatingStr);

            // --- 3. THE BUCKET & CACHE ---
            Movie internetMovie = new Movie();
            internetMovie.setImdbId(fetchedImdbId); // Added this
            internetMovie.setTitle(fetchedTitle);
            internetMovie.setGenre(fetchedGenre);
            internetMovie.setRating(rating);
            internetMovie.setReleased(true); // Default to true for OMDb movies

            return movieRepository.save(internetMovie);

        } catch (Exception e) {
            System.err.println("[CRITICAL] Internet connection failed: " + e.getMessage());
            return null;
        }
    }

    private String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\":\"";
        int startIndex = json.indexOf(searchKey) + searchKey.length();
        int endIndex = json.indexOf("\"", startIndex);
        if (startIndex < searchKey.length() || endIndex == -1) return "N/A";
        return json.substring(startIndex, endIndex);
    }

    // --- NEW: THE RECOMMENDATION ENGINE ---
    public java.util.List<Movie> getRecommendations(String title) {
        return movieRepository.getRecommendations(title);
    }
}
