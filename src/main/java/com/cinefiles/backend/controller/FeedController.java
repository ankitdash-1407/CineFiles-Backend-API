package com.cinefiles.backend.controller;

import com.cinefiles.backend.entity.Post;
import com.cinefiles.backend.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class FeedController {

    // 1. Inject the new Brain instead of calling the old static Manager
    @Autowired
    private PostService postService;

    @GetMapping("/feed")
    public List<Post> serveGlobalFeed() {
        // 2. Ask the Service for the data.
        // Spring Boot AUTOMATICALLY converts these Post objects (and their connected User/Movie objects) into JSON.
        return postService.getGlobalFeed();
    }
}
