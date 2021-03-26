package com.seventythreestrings.valuation.api.debtmodel.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DebtModel {
	private LocalDate date; 
	private DateType dateType; //O,I,P,IP
	private double principalInflow;
	private double pricipalRepayment;
	private double totalPricipalOutstanding;
	private double baseRate;
	private double spread;
	private double totalInterestRate;
	private double interestPayment;
	private double yearFrac;
	private double interestOutflow;
	private double totalCashMovement;
	@Override
	public String toString() {
		return "[date=" + date+ ", dateType=" + dateType
				+ /*
					 * ", dateType=" + dateType + ", principalInflow=" + principalInflow +
					 * ", pricipalRepayment=" + pricipalRepayment + ", totalPricipalOutstanding=" +
					 * totalPricipalOutstanding + ", baseRate=" + baseRate + ", spread=" + spread +
					 * ", totalInterestRate=" + totalInterestRate + ", interestPayment=" +
					 * interestPayment + ", yearFrac=" + yearFrac + ", interestOutflow=" +
					 * interestOutflow + ", totalCashMovement=" + totalCashMovement +
					 */ "]";
	}
	
}
