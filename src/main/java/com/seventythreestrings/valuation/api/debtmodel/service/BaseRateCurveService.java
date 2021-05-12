package com.seventythreestrings.valuation.api.debtmodel.service;

import com.seventythreestrings.valuation.api.debtmodel.model.BaseRateCurve;

import java.util.List;

public interface BaseRateCurveService {
    List<BaseRateCurve> getCurvesForBaseRate(Long baseRateId);
}
