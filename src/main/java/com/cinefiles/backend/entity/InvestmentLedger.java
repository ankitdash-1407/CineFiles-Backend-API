package com.cinefiles.backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp; // <-- NEW IMPORT
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "investment_ledger")
public class InvestmentLedger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "investment_id")
    private int investmentId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User investor;

    @ManyToOne
    @JoinColumn(name = "campaign_id", referencedColumnName = "campaign_id", nullable = false)
    private Campaign campaign;

    @Column(name = "amount_invested", nullable = false)
    private BigDecimal amountInvested;

    @Column(name = "payment_status")
    private String paymentStatus = "PENDING";

    // THE FIX: Forces Java to stamp the exact millisecond of the transaction
    @CreationTimestamp
    @Column(name = "transaction_date", updatable = false)
    private LocalDateTime transactionDate;

    // --- GETTERS AND SETTERS ---
    public int getInvestmentId() { return investmentId; }
    public void setInvestmentId(int investmentId) { this.investmentId = investmentId; }

    public User getInvestor() { return investor; }
    public void setInvestor(User investor) { this.investor = investor; }

    public Campaign getCampaign() { return campaign; }
    public void setCampaign(Campaign campaign) { this.campaign = campaign; }

    public BigDecimal getAmountInvested() { return amountInvested; }
    public void setAmountInvested(BigDecimal amountInvested) { this.amountInvested = amountInvested; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public LocalDateTime getTransactionDate() { return transactionDate; }
}
