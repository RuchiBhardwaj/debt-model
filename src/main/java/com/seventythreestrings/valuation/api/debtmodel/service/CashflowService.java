package com.seventythreestrings.valuation.api.debtmodel.service;

import com.seventythreestrings.valuation.api.debtmodel.model.Cashflow;

public interface CashflowService {
    Cashflow getCashflowForDebtModel(Long debtModelId);

    Cashflow generateCashflowForDebtModel(Long debtModelId);

    void deleteCashflowForDebtModel(Long debtModelId);
}
