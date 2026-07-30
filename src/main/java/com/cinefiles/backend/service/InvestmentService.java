package com.cinefiles.backend.service;

import com.cinefiles.backend.entity.Campaign;
import com.cinefiles.backend.entity.InvestmentLedger;
import com.cinefiles.backend.entity.Movie;
import com.cinefiles.backend.entity.User;
import com.cinefiles.backend.repository.CampaignRepository;
import com.cinefiles.backend.repository.InvestmentLedgerRepository;
import com.cinefiles.backend.repository.MovieRepository;
import com.cinefiles.backend.repository.UserRepository; // <-- NEW IMPORT
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
    private MovieRepository movieRepository;

    @Autowired
    private UserRepository userRepository; // <-- INJECTED THE USER REPOSITORY

    public Campaign launchCampaign(int movieId, BigDecimal targetAmount) {
        Movie targetMovie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie ID not found in database."));

        Campaign newCampaign = new Campaign();
        newCampaign.setMovie(targetMovie);
        newCampaign.setFundingTarget(targetAmount);
        newCampaign.setStatus("ACTIVE");

        return campaignRepository.save(newCampaign);
    }

    @Transactional
    public String processInvestment(int userId, int campaignId, BigDecimal amount) {

        Campaign campaign = campaignRepository.findById(campaignId).orElse(null);

        if (campaign == null) {
            return "FAILED: Campaign does not exist.";
        }

        if (!"ACTIVE".equals(campaign.getStatus())) {
            return "FAILED: This campaign is closed or already fully funded.";
        }

        BigDecimal newTotal = campaign.getCurrentRaised().add(amount);
        campaign.setCurrentRaised(newTotal);

        if (newTotal.compareTo(campaign.getFundingTarget()) >= 0) {
            campaign.setStatus("FUNDED");
        }

        InvestmentLedger receipt = new InvestmentLedger();

        // THE FIX: Fetch the REAL user from the database so Hibernate doesn't crash
        User investor = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));

        receipt.setInvestor(investor);
        receipt.setCampaign(campaign);
        receipt.setAmountInvested(amount);
        receipt.setPaymentStatus("SUCCESS");

        ledgerRepository.save(receipt);
        campaignRepository.save(campaign);

        return "SUCCESS: Investment locked in Escrow.";
    }
}
