package com.cinefiles.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String password;

    // ADDED: Crucial for Spring Security to handle authorization later
    @Column(name = "role", nullable = false)
    private String role = "INVESTOR";

    @ManyToMany
    @JoinTable(
            name = "watchlists",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "movie_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "movie_id"})
    )
    private java.util.List<Movie> watchlist = new java.util.ArrayList<>();

    // --- CONSTRUCTORS ---
    public User() {}

    public User(int id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    // --- GETTERS ---
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public java.util.List<Movie> getWatchlist() { return watchlist; }
    public String getPassword() { return password; }
    public String getRole() { return role; } // ADDED

    // --- SETTERS ---
    public void setId(int id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setWatchlist(java.util.List<Movie> watchlist) { this.watchlist = watchlist; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; } // ADDED
}