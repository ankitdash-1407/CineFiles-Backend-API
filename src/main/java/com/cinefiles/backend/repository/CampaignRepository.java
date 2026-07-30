package com.cinefiles.backend.repository;

import com.cinefiles.backend.entity.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Integer> {

    // Spring Boot reads this method name and automatically writes the SQL:
    // SELECT * FROM campaigns WHERE movie_id = ?
    Campaign findByMovieId(int movieId);
}
