package com.seventythreestrings.valuation.api.debtmodel.repository;


import com.seventythreestrings.valuation.api.debtmodel.model.CallPremium;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CallPremiumRepository extends JpaRepository<CallPremium,Long>, JpaSpecificationExecutor<CallPremium> {
    Optional<CallPremium> findFirstByDebtModelId(Long debtModelId);
}
