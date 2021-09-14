package com.seventythreestrings.valuation.api.debtmodel.repository;

import com.seventythreestrings.valuation.api.debtmodel.model.Cashflow;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CashflowRepository extends JpaRepository<Cashflow, Long>, JpaSpecificationExecutor<Cashflow> {
    @EntityGraph(
            type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {
                    "schedules"
            }
    )
    Optional<Cashflow> findFirstByDebtModelId(Long debtModelId);

//    @Query(value = "select gd.* from cashflow gd where gd.debt_model_id=:debtModelId and gd.version_id =:versionId",nativeQuery = true)
    List<Cashflow> findAllByDebtModelIdAndVersionId(Long debtModelId, Integer versionId);
}
