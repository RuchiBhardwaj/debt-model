package com.seventythreestrings.valuation.api.debtmodel.service.impl;

import com.seventythreestrings.valuation.api.debtmodel.model.BaseRateCurve;
import com.seventythreestrings.valuation.api.debtmodel.repository.BaseRateCurveRepository;
import com.seventythreestrings.valuation.api.debtmodel.service.BaseRateCurveService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class BaseRateCurveServiceImpl implements BaseRateCurveService {
    private final BaseRateCurveRepository repository;

    @Override
    public List<BaseRateCurve> getCurvesForBaseRate(Long baseRateId) {
        return repository.getAllByBaseRateId(baseRateId);
    }
}
