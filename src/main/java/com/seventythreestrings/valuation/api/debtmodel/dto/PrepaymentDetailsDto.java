package com.seventythreestrings.valuation.api.debtmodel.dto;

import lombok.Data;

import java.util.List;

@Data
public class PrepaymentDetailsDto {
	private Long id;

	private PrincipalRepaymentPattern principalRepaymentPattern;

	private List<PaymentScheduleDto> paymentSchedules;

	private Long debtModelId;
}
