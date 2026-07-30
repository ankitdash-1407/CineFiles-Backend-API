package com.cinefiles.backend.repository;

import com.cinefiles.backend.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    // Replaces the "getCommentsForPost" SQL query!
    List<Comment> findByPost_PostIdOrderByCreatedAtAsc(int postId);
}
