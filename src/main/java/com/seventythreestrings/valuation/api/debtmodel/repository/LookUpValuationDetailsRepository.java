package com.seventythreestrings.valuation.api.debtmodel.repository;

import com.seventythreestrings.valuation.api.debtmodel.model.LookUpValuationDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LookUpValuationDetailsRepository extends JpaRepository<LookUpValuationDetails, Long>, JpaSpecificationExecutor<LookUpValuationDetails> {

    @Override
    <S extends LookUpValuationDetails> S save(S s);

    Optional<LookUpValuationDetails> findValuationDateByValuationDateId(UUID valuationDateId);

//    Optional<LookUpValuationDetails> findFirstByValuationIdOrderByVersionIdDesc(UUID valuationId);
}
