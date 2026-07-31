package com.cinefiles.backend.repository;

import com.cinefiles.backend.entity.InvestmentLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvestmentLedgerRepository extends JpaRepository<InvestmentLedger, Integer> {

    // FIXED: "investor" matches the User object in your Ledger,
    // and "Id" matches the actual variable inside your User entity.
    List<InvestmentLedger> findByInvestorId(int id);
}
