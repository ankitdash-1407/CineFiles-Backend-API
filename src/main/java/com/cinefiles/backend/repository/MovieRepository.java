package com.cinefiles.backend.repository;

import com.cinefiles.backend.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Integer> {

    // The new Single Source of Truth
    Movie findByImdbId(String imdbId);

    // Kept this just in case you still need to query by title temporarily
    Movie findByTitle(String title);

    // Re-wired to anchor on imdb_id and exclude unreleased pitches from standard recommendations
    @Query(value = "SELECT * FROM movies WHERE genre = (SELECT genre FROM movies WHERE imdb_id = ?1 LIMIT 1) AND imdb_id != ?1 AND is_released = true ORDER BY rating DESC LIMIT 5", nativeQuery = true)
    List<Movie> getRecommendations(String targetImdbId);
}
