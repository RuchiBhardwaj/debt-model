package com.seventythreestrings.valuation.api.debtmodel.service;

import com.seventythreestrings.valuation.api.debtmodel.dto.DebtModel;
import com.seventythreestrings.valuation.api.debtmodel.dto.GeneralDetails;
import com.seventythreestrings.valuation.api.debtmodel.dto.InterestDetails;
import com.seventythreestrings.valuation.api.debtmodel.dto.InterimVariablePayment;
import com.seventythreestrings.valuation.api.exception.AppException;

import java.util.List;

public interface DebtModelService {
	List<DebtModel> computeDeptModel(String requestBody) throws AppException;
	double computeBaseRate(InterestDetails interestDetails) throws AppException;
	List<DebtModel> computeCashMovementForPaid(GeneralDetails generalDetails, InterestDetails interestDetails, List<DebtModel> debtModels,
                                               List<InterimVariablePayment> interimVariablePayments) throws AppException;
	List<DebtModel> computeCashMovementForAccrued(GeneralDetails generalDetails, InterestDetails interestDetails, List<DebtModel> debtModels,
                                                  List<InterimVariablePayment> interimVariablePayments) throws AppException;
	double computeLiboreCurve(InterestDetails interestDetails)throws AppException;
}
