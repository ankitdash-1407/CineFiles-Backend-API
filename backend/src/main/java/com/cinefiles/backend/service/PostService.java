package com.cinefiles.backend.service;

import com.cinefiles.backend.entity.Post;
import com.cinefiles.backend.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    // This completely replaces your old PostManager.getGlobalFeed()
    public List<Post> getGlobalFeed() {
        // Look how clean this is compared to the old raw SQL string!
        return postRepository.findAllByOrderByCreatedAtDesc();
    }
}
