package com.seventythreestrings.valuation.api.debtmodel.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.seventythreestrings.valuation.api.debtmodel.model.BaseRate;
import com.seventythreestrings.valuation.api.debtmodel.model.BaseRateCurve;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDate;

@Data
public class InterestDetailsDto {
	private Long id;

	private Boolean hasInterestPayment;

	private InterestType interestPaidOrAccrued;

	private InterestPayment interestPaymentType;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate firstInterestPaymentDate;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate regimeStartDate;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate regimeEndDate;

	private int versionId;

	private PaymentFrequency interestPaymentFrequency;

	@Min(0)
	@Max(28)
	private int interestPaymentDay;

	private BaseRate baseRate;

	private BaseRateCurve baseRateCurve;

	private double baseRateFloor;

	private double baseRateCap;

	private double fixedBaseRate;

	private double baseRateSpread;

	private Long debtModelId;
}
