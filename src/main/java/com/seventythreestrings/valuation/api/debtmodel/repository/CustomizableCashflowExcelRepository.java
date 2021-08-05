package com.seventythreestrings.valuation.api.debtmodel.repository;

import com.seventythreestrings.valuation.api.debtmodel.model.CustomizableCashflowExcel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomizableCashflowExcelRepository extends JpaRepository<CustomizableCashflowExcel, Long>, JpaSpecificationExecutor<CustomizableCashflowExcel> {
    Optional<CustomizableCashflowExcel> findFirstByDebtModelId(Long debtModelId);



}
