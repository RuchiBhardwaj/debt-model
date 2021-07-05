package com.seventythreestrings.valuation.api.debtmodel.repository;

import com.seventythreestrings.valuation.api.debtmodel.model.InterestUndrawnCapital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InterestUndrwanCapitalRepository extends JpaRepository<InterestUndrawnCapital,Long>, JpaSpecificationExecutor<InterestUndrawnCapital> {
    Optional<InterestUndrawnCapital> findFirstByDebtModelId(Long debtModelId);

}
