package com.seventythreestrings.valuation.api.debtmodel.service.impl;

import com.seventythreestrings.valuation.api.debtmodel.model.BaseRate;
import com.seventythreestrings.valuation.api.debtmodel.repository.BaseRateRepository;
import com.seventythreestrings.valuation.api.debtmodel.service.BaseRateService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class BaseRateServiceImpl implements BaseRateService {
    private final BaseRateRepository repository;

    @Override
    public List<BaseRate> getAll() {
        return repository.findAll();
    }
}
