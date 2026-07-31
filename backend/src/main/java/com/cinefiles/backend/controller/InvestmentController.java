package com.cinefiles.backend.controller;

import com.cinefiles.backend.entity.InvestmentLedger;
import com.cinefiles.backend.repository.InvestmentLedgerRepository;
import com.cinefiles.backend.service.InvestmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/investments")
@CrossOrigin(origins = "*")
public class InvestmentController {

    @Autowired
    private InvestmentService investmentService;

    @Autowired
    private InvestmentLedgerRepository ledgerRepository;

    // THE FIX: Inject the Razorpay Config here
    @Autowired
    private RazorpayClient razorpayClient;

    @GetMapping("/portfolio/{userId}")
    public ResponseEntity<?> getUserPortfolio(@PathVariable int userId) {
        try {
            List<InvestmentLedger> portfolio = ledgerRepository.findByInvestorId(userId);
            return ResponseEntity.ok(portfolio);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch portfolio."));
        }
    }

    // NEW PIPE: Creates the Razorpay Order for the frontend pop-up
    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> data) {
        try {
            double amount = Double.parseDouble(data.get("amount").toString());

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", (int) (amount * 100)); // Razorpay uses paise
            orderRequest.put("currency", "INR"); // Locked to INR for India test accounts
            orderRequest.put("receipt", "txn_escrow_" + System.currentTimeMillis());

            Order order = razorpayClient.orders.create(orderRequest);

            return ResponseEntity.ok(order.toString());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to create Escrow Order: " + e.getMessage());
        }
    }

    @PostMapping("/invest")
    public ResponseEntity<?> investInCampaign(@RequestBody Map<String, Object> payload) {
        try {
            int userId = Integer.parseInt(payload.get("userId").toString());
            int postId = Integer.parseInt(payload.get("postId").toString());
            BigDecimal amount = new BigDecimal(payload.get("amount").toString());

            String result = investmentService.processInvestment(userId, postId, amount);

            if (result.startsWith("SUCCESS")) {
                return ResponseEntity.ok(Map.of("message", result));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", result));
            }

        } catch (Exception e) {
            System.err.println("[CRITICAL] Payment parsing failed: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Internal Server Error."));
        }
    }
}