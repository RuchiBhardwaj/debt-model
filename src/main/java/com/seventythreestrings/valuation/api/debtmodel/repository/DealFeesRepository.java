package com.seventythreestrings.valuation.api.debtmodel.repository;

import com.seventythreestrings.valuation.api.debtmodel.model.DealFees;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DealFeesRepository extends JpaRepository<DealFees,Long>, JpaSpecificationExecutor<DealFees> {
    Optional<DealFees> findFirstByDebtModelId(Long debtModelId);

    List<DealFees> findAllByDebtModelIdAndVersionId(Long debtModelId, Integer version);

    // for latest version id
    Optional<DealFees> findFirstByDebtModelIdOrderByVersionIdDesc(Long debtModelId);
}
