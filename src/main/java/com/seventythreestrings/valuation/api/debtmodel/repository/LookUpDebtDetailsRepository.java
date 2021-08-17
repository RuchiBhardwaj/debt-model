package com.seventythreestrings.valuation.api.debtmodel.repository;

import com.seventythreestrings.valuation.api.debtmodel.model.DebtModel;
import com.seventythreestrings.valuation.api.debtmodel.model.LookUpDebtDetails;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LookUpDebtDetailsRepository extends JpaRepository<LookUpDebtDetails, Long>, JpaSpecificationExecutor<LookUpDebtDetails> {

    @Override
    <S extends LookUpDebtDetails> S save(S s);

//    Optional<LookUpDebtDetails> findById(UUID company_id);
}
