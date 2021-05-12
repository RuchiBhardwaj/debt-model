package com.seventythreestrings.valuation.api.debtmodel.dto;

public enum InterestPayment {
	CASH("CASH"),
	PAYMENT_IN_KIND("PIK"),
	PART_CASH_PART_PIK("PART_CASH_PART_PIK");

	private final String code;

	InterestPayment(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
