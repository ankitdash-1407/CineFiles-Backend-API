package com.cinefiles.backend.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private int postId;

    // --- STRICT FOREIGN KEY TO USERS ---
    // Replaces 'String authorName'
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User author;

    // --- STRICT FOREIGN KEY TO MOVIES ---
    // Replaces 'String movieTitle'
    @ManyToOne
    @JoinColumn(name = "movie_id", referencedColumnName = "movie_id")
    private Movie movie;

    @Column(name = "post_text", columnDefinition = "TEXT")
    private String text;

    @Column(name = "media_url")
    private String mediaUrl;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "like_count")
    private int likeCount = 0; // Defaulting to 0 when a post is created

    // --- CONSTRUCTORS ---
    // 1. The Empty Vault (Strictly required by Spring Data JPA!)
    public Post() {}

    // 2. Your Assembly Line (Updated to accept the Objects instead of Strings)
    public Post(User author, Movie movie, String text, String mediaUrl, Timestamp createdAt, int likeCount) {
        this.author = author;
        this.movie = movie;
        this.text = text;
        this.mediaUrl = mediaUrl;
        this.createdAt = createdAt;
        this.likeCount = likeCount;
    }

    // --- SETTER ---
    public void setPostId(int postId) {
        this.postId = postId;
    }

    // --- GETTERS ---
    public int getPostId() { return postId; }

    // To get the author's name in your code now, you will call: getAuthor().getUsername()
    public User getAuthor() { return author; }

    // To get the movie title in your code now, you will call: getMovie().getTitle()
    public Movie getMovie() { return movie; }

    public String getText() { return text; }
    public String getMediaUrl() { return mediaUrl; }
    public Timestamp getCreatedAt() { return createdAt; }
    public int getLikeCount() { return likeCount; }
}