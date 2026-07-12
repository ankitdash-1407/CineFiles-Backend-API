package com.cinefiles.backend.controller;

import com.cinefiles.backend.entity.Campaign;
import com.cinefiles.backend.repository.CampaignRepository; // Added to fetch bonds
import com.cinefiles.backend.service.InvestmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/investments") // 1. FIXED: Matched React's Base URL
@CrossOrigin(origins = "*")
public class InvestmentController {

    @Autowired
    private InvestmentService investmentService;

    @Autowired
    private CampaignRepository campaignRepository;

    // 2. FIXED: Added the missing GET endpoint to load the Bond Market UI
    @GetMapping("/campaigns")
    public ResponseEntity<?> getAllCampaigns() {
        try {
            List<Campaign> campaigns = campaignRepository.findAll();
            return ResponseEntity.ok(campaigns);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch campaigns."));
        }
    }

    // 3. FIXED: Matched React's exact URL and Query Parameters
    @PostMapping("/invest")
    public ResponseEntity<?> investInCampaign(
            @RequestParam int userId,
            @RequestParam int campaignId,
            @RequestParam BigDecimal amount) {

        try {
            // Send it to the Fintech Brain
            String result = investmentService.processInvestment(userId, campaignId, amount);

            if (result.startsWith("SUCCESS")) {
                return ResponseEntity.ok(Map.of("message", result));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", result));
            }

        } catch (Exception e) {
            System.err.println("[CRITICAL] Payment parsing failed: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Internal Server Error during transaction."));
        }
    }

    @PostMapping("/campaign/start")
    public ResponseEntity<?> startCampaign(@RequestBody Map<String, String> payload) {
        try {
            int movieId = Integer.parseInt(payload.get("movieId"));
            BigDecimal targetAmount = new BigDecimal(payload.get("targetAmount"));

            Campaign savedCampaign = investmentService.launchCampaign(movieId, targetAmount);

            return ResponseEntity.ok(Map.of(
                    "message", "SUCCESS: Campaign launched!",
                    "campaignId", savedCampaign.getCampaignId()
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}