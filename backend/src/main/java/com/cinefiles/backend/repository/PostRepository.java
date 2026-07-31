package com.cinefiles.backend.repository;

import com.cinefiles.backend.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    // THE FIX: Using 'author' instead of 'user' to match your Post entity
    @Query("SELECT p FROM Post p JOIN FETCH p.author LEFT JOIN FETCH p.movie ORDER BY p.createdAt DESC")
    List<Post> findAllByOrderByCreatedAtDesc();
}
