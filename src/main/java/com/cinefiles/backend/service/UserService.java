package com.cinefiles.backend.service;

import com.cinefiles.backend.entity.Movie;
import com.cinefiles.backend.entity.User;
import com.cinefiles.backend.repository.UserRepository;
import com.cinefiles.backend.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.util.Base64;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

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

    // --- JPA REGISTRATION LOGIC ---
    public User registerUser(String username, String email, String rawPassword) {
        String securedPassword = hashPassword(rawPassword);

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(securedPassword);

        return userRepository.save(newUser);
    }

    // --- JPA LOGIN LOGIC ---
    public User verifyLogin(String username, String rawPassword) {
        User user = userRepository.findByUsername(username);

        if (user != null) {
            String securedPassword = hashPassword(rawPassword);
            if (securedPassword.equals(user.getPassword())) {
                return user;
            }
        }
        return null;
    }

    // --- JPA WATCHLIST LOGIC (With Transactional Fix) ---
    @Transactional
    public void addMovieToWatchlist(String username, String title) {
        User user = userRepository.findByUsername(username);
        if (user == null) throw new RuntimeException("User not found.");

        Movie movie = movieRepository.findByTitle(title);
        if (movie == null) throw new RuntimeException("Movie not found. Search for it first!");

        if (!user.getWatchlist().contains(movie)) {
            user.getWatchlist().add(movie);
            userRepository.save(user);
        } else {
            throw new RuntimeException("Movie is already in your watchlist!");
        }
    }

    // --- NEW: FETCH WATCHLIST ---
    public List<Movie> getWatchlist(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) throw new RuntimeException("User not found");
        return user.getWatchlist();
    }
}