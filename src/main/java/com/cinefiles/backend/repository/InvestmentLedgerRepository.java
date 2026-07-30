package com.cinefiles.backend.repository;

import com.cinefiles.backend.entity.InvestmentLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InvestmentLedgerRepository extends JpaRepository<InvestmentLedger, Integer> {

    // Notice the underscore: This tells Spring to look for the "investor" object,
    // and then check its internal "id" field.
    List<InvestmentLedger> findByInvestor_Id(int userId);
}
