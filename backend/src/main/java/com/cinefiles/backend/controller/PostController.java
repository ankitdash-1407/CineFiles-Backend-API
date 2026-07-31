package com.cinefiles.backend.controller;

import com.cinefiles.backend.entity.Post;
import com.cinefiles.backend.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "*")
public class PostController {

    @Autowired
    private PostService postService;

    // 1. GET THE FEED: Returns every post (social or campaign)
    @GetMapping("/feed")
    public List<Post> getGlobalFeed() {
        return postService.getGlobalFeed();
    }

    // 2. CREATE POST/CAMPAIGN: This handles everything unified
    @PostMapping("/create")
    public ResponseEntity<?> createPost(@RequestBody Map<String, Object> payload) {
        try {
            int userId = Integer.parseInt(payload.get("userId").toString());
            int movieId = Integer.parseInt(payload.get("movieId").toString());
            String text = payload.get("text").toString();

            // Check if this is a campaign post
            boolean isCampaign = (boolean) payload.getOrDefault("isCampaign", false);
            BigDecimal target = isCampaign ? new BigDecimal(payload.get("fundingTarget").toString()) : BigDecimal.ZERO;

            Post newPost = postService.createPost(userId, movieId, text, "", isCampaign, target);
            return ResponseEntity.ok(newPost);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to create post: " + e.getMessage()));
        }
    }
}
