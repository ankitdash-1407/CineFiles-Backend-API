package com.cinefiles.backend.repository;

import com.cinefiles.backend.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Integer> {

    // Replaces getMovieId() and searchLocalDatabase()
    Movie findByTitle(String title);

    // Replaces getRecommendations() - using your exact SQL but hooked into JPA!
    @Query(value = "SELECT * FROM movies WHERE genre = (SELECT genre FROM movies WHERE title = ?1 LIMIT 1) AND title != ?1 ORDER BY rating DESC LIMIT 5", nativeQuery = true)
    List<Movie> getRecommendations(String searchedTitle);
}
