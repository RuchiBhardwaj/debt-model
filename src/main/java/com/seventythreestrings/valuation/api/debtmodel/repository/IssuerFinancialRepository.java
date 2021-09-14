package com.seventythreestrings.valuation.api.debtmodel.repository;

import com.seventythreestrings.valuation.api.debtmodel.model.IssuerFinancial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IssuerFinancialRepository extends JpaRepository<IssuerFinancial,Long>, JpaSpecificationExecutor<IssuerFinancial> {
    Optional<IssuerFinancial> findFirstByDebtModelId(Long debtModelId);

    List<IssuerFinancial> findAllByDebtModelIdAndVersionId(Long debtModelId, Integer version);

    IssuerFinancial findByDebtModelIdAndVersionId(Long debModelId, Integer version);

    // for latest version id
    Optional<IssuerFinancial> findFirstByDebtModelIdOrderByVersionIdDesc(Long debtModelId);
}
