package com.seventythreestrings.valuation.api.debtmodel.repository;

import com.seventythreestrings.valuation.api.debtmodel.model.GeneralDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GeneralDetailsRepository extends JpaRepository<GeneralDetails, Long>, JpaSpecificationExecutor<GeneralDetails> {

    Optional<GeneralDetails> findFirstByDebtModelId(Long debtModelId);
}
