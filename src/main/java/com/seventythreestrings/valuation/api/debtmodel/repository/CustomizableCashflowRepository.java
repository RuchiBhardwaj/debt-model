package com.seventythreestrings.valuation.api.debtmodel.repository;

import com.seventythreestrings.valuation.api.debtmodel.model.CustomizableCashflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomizableCashflowRepository extends JpaRepository<CustomizableCashflow, Long>, JpaSpecificationExecutor<CustomizableCashflow> {
    List<CustomizableCashflow> findAllByDebtModelIdAndVersionId(Long debtModelId, Integer version);

    Optional<CustomizableCashflow> findFirstByDebtModelId(Long debtModelId);

    Optional<CustomizableCashflow> findFirstByDebtModelIdOrderByVersionIdDesc(Long debtModelId);
}
