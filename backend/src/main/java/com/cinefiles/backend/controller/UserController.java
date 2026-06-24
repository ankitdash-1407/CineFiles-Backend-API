package com.cinefiles.backend.controller;

import com.cinefiles.backend.entity.User;
import com.cinefiles.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // Fixes CORS issues during local testing
public class UserController {

    // 1. Inject the new Brain!
    @Autowired
    private UserService userService;

    // --- REGISTER ENDPOINT ---
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String email = payload.get("email");
        String password = payload.get("password");

        if (username == null || password == null || email == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing required fields"));
        }

        try {
            // 2. Use the new Service to save the user
            User newUser = userService.registerUser(username, email, password);

            // SECURITY: Wipe the password from memory before sending the JSON back to the frontend!
            newUser.setPassword(null);

            return ResponseEntity.ok(newUser);
        } catch (Exception e) {
            // If JPA throws an error (like a duplicate username), catch it cleanly
            return ResponseEntity.badRequest().body(Map.of("error", "Registration failed. Username or email may already exist."));
        }
    }

    // --- LOGIN ENDPOINT ---
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String password = payload.get("password");

        if (username == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username and password are required"));
        }

        // 3. Use the new Service to verify the hash
        User loggedInUser = userService.verifyLogin(username, password);

        if (loggedInUser != null) {
            // SECURITY: Wipe the password hash before handing the User object to the frontend
            loggedInUser.setPassword(null);
            return ResponseEntity.ok(loggedInUser);
        } else {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid username or password"));
        }
    }
}