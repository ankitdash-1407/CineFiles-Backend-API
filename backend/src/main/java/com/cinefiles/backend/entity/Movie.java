package com.cinefiles.backend.entity; // Make sure the package is correct!

import jakarta.persistence.*;

@Entity
@Table(name = "movies")
public class Movie {

    // --- DATABASE MAPPING ---
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id")
    private int id;

    @Column(name = "title", unique = true, nullable = false)
    private String title;

    @Column(name = "genre")
    private String genre;

    @Column(name = "rating")
    private double rating;

    // --- CONSTRUCTORS ---
    public Movie() {} // Required by JPA

    public Movie(int id, String title, String genre, double rating) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.rating = rating;
    }

    // --- GETTERS ---
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getGenre() { return genre; }
    public double getRating() { return rating; }

    // --- SETTERS ---
    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setRating(double rating) { this.rating = rating; }
}