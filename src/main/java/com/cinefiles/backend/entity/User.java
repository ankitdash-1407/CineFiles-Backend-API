package com.cinefiles.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int id;

    @Column(name = "username",unique = true, nullable = false)
    private String username;

    @Column(name = "email",unique = true, nullable = false)
    private String email;

    // --- THE WATCHLIST (Auto-Bridge Table) ---
    @ManyToMany
    @JoinTable(
            name = "watchlists",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "movie_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "movie_id"}) // <-- THE TITANIUM LOCK
    )
    private java.util.List<Movie> watchlist = new java.util.ArrayList<>();

    @Column(name = "password_hash") // Now it perfectly targets your existing column!
    private String password;




    // --- CONSTRUCTORS ---
    // JPA strictly requires an empty constructor to build the entity
    public User() {}

    // Your original constructor (keep this if your manual JDBC UserManager still uses it)
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

    // --- SETTERS ---
    public void setId(int id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setWatchlist(java.util.List<Movie> watchlist) { this.watchlist = watchlist; }
    public void setPassword(String password) { this.password = password; }
}