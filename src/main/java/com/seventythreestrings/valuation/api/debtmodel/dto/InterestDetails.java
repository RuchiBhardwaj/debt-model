package com.seventythreestrings.valuation.api.debtmodel.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class InterestDetails {
	private String interestPayment;
	private String interestPaidOrAccrued;// Paid/Accrued
	private String interestPaidType;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate firstInterestPaymentDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate lastInterestPaymentDate;
	private String interestPaymentFrequency;
	private int interestPaymentDay;
	private String baseRate;
	private String liborCurve;
	private double floor;
	private double cap;
	private double fixedBaseRate;
	private double spreadOverBaseRate;
}
