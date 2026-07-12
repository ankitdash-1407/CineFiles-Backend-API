package com.cinefiles.backend.repository;

import com.cinefiles.backend.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    // This entirely replaces your massive 'getGlobalFeed' SQL string!
    // It automatically fetches all posts, ordered newest to oldest.
    List<Post> findAllByOrderByCreatedAtDesc();
}
