package com.seventythreestrings.valuation.api.debtmodel.repository;

import com.seventythreestrings.valuation.api.debtmodel.model.Skims;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SkimsRepository extends JpaRepository<Skims, Long>, JpaSpecificationExecutor<Skims> {
    Optional<Skims> findFirstByDebtModelId(Long debtModelId);


}
