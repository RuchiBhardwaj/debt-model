package com.seventythreestrings.valuation.api.debtmodel.dto;

import lombok.Data;

import java.util.List;

@Data
public class PrepaymentDetails {
	private String principalRepaymentPattern;
	private List<InterimVariablePayment> interimVariablePayments;
	
}
