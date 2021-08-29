package com.seventythreestrings.valuation.api.debtmodel.enums;

public enum InterestType {
	PAID("Paid"),
	ACCRUED("Accrued");

	private final String code;

	InterestType(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
