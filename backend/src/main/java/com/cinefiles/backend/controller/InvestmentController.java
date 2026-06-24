package com.cinefiles.backend.controller;

import com.cinefiles.backend.entity.Campaign;
import com.cinefiles.backend.service.InvestmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/invest")
@CrossOrigin(origins = "*") // Keep CORS open for local testing
public class InvestmentController {

    @Autowired
    private InvestmentService investmentService;

    @PostMapping
    public ResponseEntity<?> investInCampaign(@RequestBody Map<String, String> payload) {
        try {
            // 1. Extract the data from the incoming JSON
            int userId = Integer.parseInt(payload.get("userId"));
            int campaignId = Integer.parseInt(payload.get("campaignId"));
            BigDecimal amount = new BigDecimal(payload.get("amount"));

            // 2. Send it to the Fintech Brain
            String result = investmentService.processInvestment(userId, campaignId, amount);

            // 3. Return the result to the user
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
    public ResponseEntity<?> startCampaign(@RequestBody java.util.Map<String, String> payload) {
        try {
            // 1. Extract the data from the JSON request
            int movieId = Integer.parseInt(payload.get("movieId"));
            java.math.BigDecimal targetAmount = new java.math.BigDecimal(payload.get("targetAmount"));

            // 2. Tell the Service to launch it
            Campaign savedCampaign = investmentService.launchCampaign(movieId, targetAmount);

            // 3. Return a success receipt to the frontend
            return ResponseEntity.ok(java.util.Map.of(
                    "message", "SUCCESS: Campaign launched!",
                    "campaignId", savedCampaign.getCampaignId()
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }
}