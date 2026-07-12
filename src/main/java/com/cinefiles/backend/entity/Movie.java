package com.cinefiles.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id")
    private int id;

    // THE NEW ABSOLUTE LOCK: No duplicate IMDb IDs allowed
    @Column(name = "imdb_id", unique = true)
    private String imdbId;

    // Removed the unique constraint here.
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "genre")
    private String genre;

    @Column(name = "rating")
    private double rating;

    // True for OMDb movies, False for custom Director pitches
    @Column(name = "is_released", nullable = false)
    private boolean isReleased = true;

    // --- CONSTRUCTORS ---
    public Movie() {}

    public Movie(int id, String imdbId, String title, String genre, double rating, boolean isReleased) {
        this.id = id;
        this.imdbId = imdbId;
        this.title = title;
        this.genre = genre;
        this.rating = rating;
        this.isReleased = isReleased;
    }

    // --- GETTERS ---
    public int getId() { return id; }
    public String getImdbId() { return imdbId; }
    public String getTitle() { return title; }
    public String getGenre() { return genre; }
    public double getRating() { return rating; }
    public boolean isReleased() { return isReleased; }

    // --- SETTERS ---
    public void setId(int id) { this.id = id; }
    public void setImdbId(String imdbId) { this.imdbId = imdbId; }
    public void setTitle(String title) { this.title = title; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setRating(double rating) { this.rating = rating; }
    public void setReleased(boolean released) { isReleased = released; }
}