package com.cinefiles.backend.service;


import com.cinefiles.backend.entity.Campaign;
import com.cinefiles.backend.entity.InvestmentLedger;
import com.cinefiles.backend.entity.Movie;
import com.cinefiles.backend.entity.User;
import com.cinefiles.backend.repository.CampaignRepository;
import com.cinefiles.backend.repository.InvestmentLedgerRepository;
import com.cinefiles.backend.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class InvestmentService {

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private InvestmentLedgerRepository ledgerRepository;

    @Autowired
    private MovieRepository movieRepository; // <-- Injecting the new translator

    // --- THE NEW LAUNCH METHOD ---
    public Campaign launchCampaign(int movieId, BigDecimal targetAmount) {
        // 1. Fetch the movie from the database. If it doesn't exist, crash the transaction cleanly.
        Movie targetMovie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie ID not found in database."));

        // 2. Build the Campaign
        Campaign newCampaign = new Campaign();
        newCampaign.setMovie(targetMovie); // Locks the strict Foreign Key
        newCampaign.setFundingTarget(targetAmount);
        newCampaign.setStatus("ACTIVE");

        // 3. Save it to MySQL
        return campaignRepository.save(newCampaign);
    }

    // --- THE FINTECH ESCROW ENGINE ---
    // @Transactional means "All or Nothing". If the server crashes on line 40,
    // it rolls back the database so nobody loses their money.
    @Transactional
    public String processInvestment(int userId, int campaignId, BigDecimal amount) {

        // 1. Fetch the Campaign
        // If two people hit this at the exact same time, Spring fetches the current @Version for both.
        Campaign campaign = campaignRepository.findById(campaignId).orElse(null);

        if (campaign == null) {
            return "FAILED: Campaign does not exist.";
        }

        // 2. Security Check: Is the campaign still accepting money?
        if (!"ACTIVE".equals(campaign.getStatus())) {
            return "FAILED: This campaign is closed or already fully funded.";
        }

        // 3. Add the money to the pool
        BigDecimal newTotal = campaign.getCurrentRaised().add(amount);
        campaign.setCurrentRaised(newTotal);

        // 4. Milestone Check: Did this investment hit the target?
        if (newTotal.compareTo(campaign.getFundingTarget()) >= 0) {
            campaign.setStatus("FUNDED"); // Lock the vault
        }

        // 5. Generate the Digital Receipt
        // 5. Generate the Digital Receipt
        InvestmentLedger receipt = new InvestmentLedger();

        // Create a temporary User object just to hold the ID for the database link
        User investor = new User();
        investor.setId(userId); // Assuming you added setId() to your User class!

        receipt.setInvestor(investor); // Set the whole User object
        receipt.setCampaign(campaign); // Set the whole Campaign object (we fetched this in Step 1)
        receipt.setAmountInvested(amount);
        receipt.setPaymentStatus("SUCCESS"); // In a real app, you wait for Razorpay to confirm this

        // 6. Save the receipt
        ledgerRepository.save(receipt);

        // 7. Save the Campaign (THE ANTI-GLITCH LOCK TRIGGERS HERE)
        // Spring saves the new total and increments the @Version by 1.
        // If the second user tries to save their old @Version now, the database throws an error
        // and safely rejects their transaction!
        campaignRepository.save(campaign);

        return "SUCCESS: Investment locked in Escrow.";
    }
}
