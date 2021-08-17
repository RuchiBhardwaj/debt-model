package com.seventythreestrings.valuation.api.debtmodel.repository;

import com.seventythreestrings.valuation.api.debtmodel.model.LookUpDebtDetails;
import com.seventythreestrings.valuation.api.debtmodel.model.LookUpValuationDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LookUpValuationDetailsRepository extends JpaRepository<LookUpValuationDetails, Long>, JpaSpecificationExecutor<LookUpValuationDetails> {

    @Override
    <S extends LookUpValuationDetails> S save(S s);
}
