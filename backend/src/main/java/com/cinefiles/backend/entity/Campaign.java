package com.cinefiles.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "campaigns")
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "campaign_id")
    private int campaignId;

    @OneToOne
    @JoinColumn(name = "movie_id", referencedColumnName = "movie_id", nullable = false, unique = true)
    private Movie movie;

    @Column(name = "funding_target", nullable = false)
    private BigDecimal fundingTarget;

    @Column(name = "current_raised")
    private BigDecimal currentRaised = BigDecimal.ZERO;

    @Column(name = "status")
    private String status = "ACTIVE"; // ACTIVE, FUNDED, FAILED

    // --- THE FINTECH LOCK ---
    // Every time this row gets updated, Spring Boot automatically increments this number.
    // If two users try to update version 1 at the same time, the first one makes it version 2.
    // The second user's update fails because it's still looking for version 1.
    @Version
    @Column(name = "version")
    private Integer version;

    // --- GETTERS AND SETTERS ---
    public int getCampaignId() { return campaignId; }
    public void setCampaignId(int campaignId) { this.campaignId = campaignId; }

    public Movie getMovie() { return movie; }
    public void setMovie(Movie movie) { this.movie = movie; }

    public BigDecimal getFundingTarget() { return fundingTarget; }
    public void setFundingTarget(BigDecimal fundingTarget) { this.fundingTarget = fundingTarget; }

    public BigDecimal getCurrentRaised() { return currentRaised; }
    public void setCurrentRaised(BigDecimal currentRaised) { this.currentRaised = currentRaised; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
