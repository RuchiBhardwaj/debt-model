package com.seventythreestrings.valuation.api.debtmodel.service;

import com.seventythreestrings.valuation.api.debtmodel.dto.DebtModelInput;
import com.seventythreestrings.valuation.api.debtmodel.dto.DebtModelInputDto;

import java.util.List;

public interface DebtModelInputService {

	List<DebtModelInputDto> getInputsForDebtModel(Long debtModelId);

	Object get(DebtModelInput inputType, Long id);

	Object create(DebtModelInput inputType, Object o, Long debtModelId);

	Object update(DebtModelInput inputType, Object o, Long debtModelId);

	void delete(DebtModelInput inputType, Long id, Long debtModelId);
}
