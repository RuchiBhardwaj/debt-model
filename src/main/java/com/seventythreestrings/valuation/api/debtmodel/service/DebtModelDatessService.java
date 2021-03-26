package com.seventythreestrings.valuation.api.debtmodel.service;

import com.seventythreestrings.valuation.api.debtmodel.dto.DebtModel;
import com.seventythreestrings.valuation.api.debtmodel.dto.GeneralDetails;
import com.seventythreestrings.valuation.api.debtmodel.dto.InterestDetails;
import com.seventythreestrings.valuation.api.debtmodel.dto.PrepaymentDetails;
import com.seventythreestrings.valuation.api.exception.AppException;

import java.util.List;

public interface DebtModelDatessService {
	
	List<DebtModel> getDatePlotsFromDTO(GeneralDetails generalDetails, InterestDetails interestDetails,
                                        PrepaymentDetails prepaymentDetails) throws AppException;
}
