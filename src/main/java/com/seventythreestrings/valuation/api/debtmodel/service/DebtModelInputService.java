package com.seventythreestrings.valuation.api.debtmodel.service;

import com.seventythreestrings.valuation.api.debtmodel.dto.*;

import java.util.List;

public interface DebtModelInputService {

	List<DebtModelInputDto> getInputsForDebtModel(Long debtModelId);

	Object get(DebtModelInput inputType, Long id);

	Object create(DebtModelInput inputType, Object o, Long debtModelId);

	Object update(DebtModelInput inputType, Object o, Long debtModelId);

	void delete(DebtModelInput inputType, Long id, Long debtModelId);

	DiscountRateComputationDto createDiscount(DiscountRateComputationDto discountRateComputationDto);

	Object createCustomizableCashflow(CashflowDates cashflowDatesType, Object o, Long debtModelId);

	List<CustomizableDto> getCustomizationCashflowData(Long debtModelId, CashflowDates cashflowDates);

	Object updateCustomizableCashflow(CashflowDates cashflowDates, Object o, Long debtModelId);

	}
