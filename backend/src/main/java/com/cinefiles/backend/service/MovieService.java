package com.cinefiles.backend.service;

import com.cinefiles.backend.entity.Movie;
import com.cinefiles.backend.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    private static final String API_KEY = System.getenv("OMDB_API_KEY");

    public Movie fetchAndCacheMovie(String title) {
        // --- 1. THE CACHE CHECK (Using your new Repository!) ---
        Movie existingMovie = movieRepository.findByTitle(title);

        if (existingMovie != null) {
            System.out.println("[⚡ CACHE HIT] '" + title + "' found in Vault! Skipping internet call.");
            return existingMovie;
        }

        // --- 2. THE CACHE MISS (Connect to OMDb) ---
        System.out.println("[🌐 CACHE MISS] Movie not found locally. Connecting to OMDb API...");
        String formattedTitle = title.replace(" ", "+");
        String url = "http://www.omdbapi.com/?t=" + formattedTitle + "&apikey=" + API_KEY;

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String jsonResponse = response.body();

            if (jsonResponse.contains("\"Response\":\"False\"")) {
                return null;
            }

            // Extract the groceries
            String fetchedTitle = extractJsonValue(jsonResponse, "Title");
            String fetchedGenre = extractJsonValue(jsonResponse, "Genre");
            String imdbRatingStr = extractJsonValue(jsonResponse, "imdbRating");
            double rating = imdbRatingStr.equals("N/A") ? 0.0 : Double.parseDouble(imdbRatingStr);

            // --- 3. THE BUCKET & CACHE: Save directly to MySQL via JPA ---
            Movie internetMovie = new Movie();
            internetMovie.setTitle(fetchedTitle);
            internetMovie.setGenre(fetchedGenre);
            internetMovie.setRating(rating);

            // Save and return the generated object all in one step!
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
