package com.seventythreestrings.valuation.api.debtmodel.repository;

import com.seventythreestrings.valuation.api.debtmodel.model.Skims;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkimsRepository extends JpaRepository<Skims, Long>, JpaSpecificationExecutor<Skims> {
    Optional<Skims> findFirstByDebtModelId(Long debtModelId);

    List<Skims> findAllByDebtModelIdAndVersionId(Long debtModelId, Integer version);

    // for latest version id
    Optional<Skims> findFirstByDebtModelIdOrderByVersionIdDesc(Long debtModelId);
}
