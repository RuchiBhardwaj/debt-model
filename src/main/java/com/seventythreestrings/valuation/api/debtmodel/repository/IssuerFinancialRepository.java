package com.seventythreestrings.valuation.api.debtmodel.repository;

import com.seventythreestrings.valuation.api.debtmodel.model.IssuerFinancial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface IssuerFinancialRepository extends JpaRepository<IssuerFinancial,Long>, JpaSpecificationExecutor<IssuerFinancial> {

}
