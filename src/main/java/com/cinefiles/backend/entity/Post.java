package com.cinefiles.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private int postId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User author;

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
    private int likeCount = 0;

    // --- CROWDFUNDING FIELDS (Now integrated!) ---
    @Column(name = "is_campaign")
    private boolean isCampaign = false;

    @Column(name = "funding_target")
    private BigDecimal fundingTarget = BigDecimal.ZERO;

    @Column(name = "current_raised")
    private BigDecimal currentRaised = BigDecimal.ZERO;

    @Column(name = "status")
    private String status = "NONE"; // ACTIVE, FUNDED, NONE

    // VERSION LOCK: Prevents data corruption during high-traffic updates
    @Version
    private int version;

    // --- CONSTRUCTORS ---
    public Post() {}

    public Post(User author, Movie movie, String text, String mediaUrl) {
        this.author = author;
        this.movie = movie;
        this.text = text;
        this.mediaUrl = mediaUrl;
    }

    // --- GETTERS & SETTERS ---
    public int getPostId() { return postId; }
    public User getAuthor() { return author; }
    public Movie getMovie() { return movie; }
    public String getText() { return text; }
    public String getMediaUrl() { return mediaUrl; }
    public Timestamp getCreatedAt() { return createdAt; }
    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }

    public boolean isCampaign() { return isCampaign; }
    public void setCampaign(boolean campaign) { isCampaign = campaign; }
    public BigDecimal getFundingTarget() { return fundingTarget; }
    public void setFundingTarget(BigDecimal fundingTarget) { this.fundingTarget = fundingTarget; }
    public BigDecimal getCurrentRaised() { return currentRaised; }
    public void setCurrentRaised(BigDecimal currentRaised) { this.currentRaised = currentRaised; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}