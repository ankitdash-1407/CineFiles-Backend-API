package com.cinefiles.backend.service;

import com.cinefiles.backend.entity.Movie;
import com.cinefiles.backend.entity.User;
import com.cinefiles.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.security.MessageDigest;
import java.util.Base64;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // --- YOUR EXACT SAME HASHING ENGINE ---
    public String hashPassword(String plainText) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(plainText.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (Exception e) {
            throw new RuntimeException("Hashing Engine Failed", e);
        }
    }

    // --- NEW: JPA REGISTRATION LOGIC ---
    public User registerUser(String username, String email, String rawPassword) {
        // 1. Hash the password
        String securedPassword = hashPassword(rawPassword);

        // 2. Build the User Bucket
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(securedPassword); // Safe to store!

        // 3. Save directly to MySQL via Spring Data JPA
        return userRepository.save(newUser);
    }
    // --- NEW: JPA LOGIN LOGIC ---
    public User verifyLogin(String username, String rawPassword) {
        // 1. Ask the Repository to find the user in MySQL
        User user = userRepository.findByUsername(username);

        // 2. If the user exists, check the password
        if (user != null) {
            String securedPassword = hashPassword(rawPassword);
            // Compare the hashes!
            if (securedPassword.equals(user.getPassword())) {
                return user; // Login successful!
            }
        }
        return null; // Login failed (wrong username or password)
    }

    @Autowired
    private com.cinefiles.backend.repository.MovieRepository movieRepository;

    // --- NEW: JPA WATCHLIST LOGIC ---
    public void addMovieToWatchlist(String username, String title) {
        // 1. Fetch the User and Movie objects directly from MySQL
        User user = userRepository.findByUsername(username);
        if (user == null) throw new RuntimeException("User not found.");

        Movie movie = movieRepository.findByTitle(title);
        if (movie == null) throw new RuntimeException("Movie not found. Search for it first!");

        // 2. Add to the list if they don't already have it
        if (!user.getWatchlist().contains(movie)) {
            user.getWatchlist().add(movie);
            userRepository.save(user); // Hibernate handles the MySQL Bridge Table automatically!
        } else {
            throw new RuntimeException("Movie is already in your watchlist!");
        }
    }
}
