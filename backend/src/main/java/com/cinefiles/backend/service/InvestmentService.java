package com.cinefiles.backend.service;

import com.cinefiles.backend.entity.InvestmentLedger;
import com.cinefiles.backend.entity.Post;
import com.cinefiles.backend.entity.User;
import com.cinefiles.backend.repository.InvestmentLedgerRepository;
import com.cinefiles.backend.repository.PostRepository;
import com.cinefiles.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class InvestmentService {

    @Autowired
    private PostRepository postRepository; // <-- SWAPPED TO POSTS

    @Autowired
    private InvestmentLedgerRepository ledgerRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public String processInvestment(int userId, int postId, BigDecimal amount) {

        // 1. Fetch the campaign from the unified Post table
        Post campaignPost = postRepository.findById(postId).orElse(null);

        if (campaignPost == null || !campaignPost.isCampaign()) {
            return "FAILED: Campaign does not exist.";
        }

        if (!"ACTIVE".equals(campaignPost.getStatus())) {
            return "FAILED: This campaign is closed or already fully funded.";
        }

        // 2. Add the money
        BigDecimal newTotal = campaignPost.getCurrentRaised().add(amount);
        campaignPost.setCurrentRaised(newTotal);

        if (newTotal.compareTo(campaignPost.getFundingTarget()) >= 0) {
            campaignPost.setStatus("FUNDED");
        }

        // 3. Create the receipt
        InvestmentLedger receipt = new InvestmentLedger();

        User investor = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));

        receipt.setInvestor(investor);
        receipt.setPost(campaignPost); // <-- UPDATED to link to Post
        receipt.setAmountInvested(amount);
        receipt.setPaymentStatus("SUCCESS");

        ledgerRepository.save(receipt);
        postRepository.save(campaignPost);

        return "SUCCESS: Investment locked in Escrow.";
    }
}
