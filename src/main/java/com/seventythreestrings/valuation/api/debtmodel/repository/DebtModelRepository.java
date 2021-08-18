package com.seventythreestrings.valuation.api.debtmodel.repository;

import com.seventythreestrings.valuation.api.debtmodel.model.DebtModel;
import com.seventythreestrings.valuation.api.debtmodel.model.Skims;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DebtModelRepository extends JpaRepository<DebtModel, Long>, JpaSpecificationExecutor<DebtModel> {

    @Override
    @EntityGraph(DebtModel.WITH_INPUTS)
    List<DebtModel> findAll();

    @EntityGraph(DebtModel.WITH_GENERAL_DETAILS_AND_CASHFLOW)
    Page<DebtModel> findAll(Pageable pageable);

    @Override
    @EntityGraph(DebtModel.WITH_INPUTS)
    Optional<DebtModel> findById(Long id);

    List<DebtModel> findAllByFundId(Long fundId);

    @Override
    @EntityGraph(DebtModel.WITH_INPUTS)
    <S extends DebtModel> S save(S s);
}
