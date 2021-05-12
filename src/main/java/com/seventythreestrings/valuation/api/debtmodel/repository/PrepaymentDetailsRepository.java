package com.seventythreestrings.valuation.api.debtmodel.repository;

import com.seventythreestrings.valuation.api.debtmodel.model.PrepaymentDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrepaymentDetailsRepository extends JpaRepository<PrepaymentDetails, Long>, JpaSpecificationExecutor<PrepaymentDetails> {
    Optional<PrepaymentDetails> findFirstByDebtModelId(Long debtModelId);
}
