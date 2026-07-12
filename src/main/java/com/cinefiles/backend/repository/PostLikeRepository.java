package com.cinefiles.backend.repository;

import com.cinefiles.backend.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Integer> {
    // Replaces the manual COUNT(*) SQL query!
    int countByPost_PostId(int postId);

    // Checks if a user already liked a post
    boolean existsByUser_IdAndPost_PostId(int userId, int postId);
}
