package com.seventythreestrings.valuation.api.debtmodel.service;

import com.seventythreestrings.valuation.api.debtmodel.dto.*;
import com.seventythreestrings.valuation.api.debtmodel.enums.SortOrder;
import com.seventythreestrings.valuation.api.debtmodel.model.DebtModel;
import com.seventythreestrings.valuation.api.debtmodel.model.LookUpDebtDetails;
import com.seventythreestrings.valuation.api.debtmodel.model.LookUpValuationDetails;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface DebtModelService{

	List<DebtModel> getAll();

	Page<DebtModel> getAllPaginatedWithGeneralDetailsAndCashflow(String sortField, SortOrder sortOrder, int pageNumber, int pageSize);

	DebtModel get(Long id);

	List<DebtModelDto> getListOfDebtModels(Long portfolioId);

	DebtModel create(DebtModel model);

	DebtModel update(DebtModel model);

	void delete(Long id);

	void save(DebtModel model);

	//Lookup
	LookUpDebtDetails saveLookUpDebtDetail(LookUpDebtDetails model);

	LookUpValuationDetails saveLookUpValuationDetails(LookUpValuationDetails lookUpValuationDetails);

	CompanyDetailsDto getCompany(UUID companyId);

	FundDetailsResponseDto getFundDetails(FundDetailsDto fundDetailsDto);

	public LookUpValuationDetails updateLookUpValuationDetails(LookUpValuationDetails lookUpValuationDetails);
}
