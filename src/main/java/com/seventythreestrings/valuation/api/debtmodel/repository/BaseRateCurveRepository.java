package com.seventythreestrings.valuation.api.debtmodel.repository;

import com.seventythreestrings.valuation.api.debtmodel.model.BaseRateCurve;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BaseRateCurveRepository extends JpaRepository<BaseRateCurve, Long>, JpaSpecificationExecutor<BaseRateCurve> {
    List<BaseRateCurve> getAllByBaseRateId(Long baseRateId);
}
