package com.cinefiles.backend.service;

import com.cinefiles.backend.entity.Movie;
import com.cinefiles.backend.entity.Post;
import com.cinefiles.backend.entity.User;
import com.cinefiles.backend.repository.MovieRepository;
import com.cinefiles.backend.repository.PostRepository;
import com.cinefiles.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    // Fetch the global feed
    public List<Post> getGlobalFeed() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    // Unified creator for Social Posts AND Campaign Posts
    public Post createPost(int userId, int movieId, String text, String mediaUrl,
                           boolean isCampaign, BigDecimal fundingTarget) {

        // Fetch valid author and movie entities from DB
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        // Create the Post
        Post post = new Post(author, movie, text, mediaUrl);

        // If it's a campaign, unlock the financial fields
        if (isCampaign) {
            post.setCampaign(true);
            post.setFundingTarget(fundingTarget);
            post.setCurrentRaised(BigDecimal.ZERO); // Always start at 0
            post.setStatus("ACTIVE");
        }

        return postRepository.save(post);
    }
}
