package com.seventythreestrings.valuation.api.debtmodel.repository;

import com.seventythreestrings.valuation.api.debtmodel.model.InterestDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InterestDetailsRepository extends JpaRepository<InterestDetails, Long>, JpaSpecificationExecutor<InterestDetails> {
    Optional<InterestDetails> findFirstByDebtModelId(Long debtModelId);
}
