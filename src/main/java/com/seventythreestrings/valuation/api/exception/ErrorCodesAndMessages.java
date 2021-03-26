package com.seventythreestrings.valuation.api.exception;

public enum ErrorCodesAndMessages {
	DEBT_MODEL_DATE_ERROR("DME00001","Exception While calculating  DebtModelDate "),
	DEBT_MODEL_ERROR("DME00002","Exception while computing debt model"),
	DEBT_MODEL_BASE_RATE_ERROR("DME00003","Exception while computing base rate"),
	DEBT_MODEL_LIBORE_CURVE_ERROR("DME00004","Exception while computing LIBORE Curve"),
	DEBT_MODEL_CASH_MOVEMENT_ERROR("DME00005","Exception while computing cash movement"),
	DEBT_MODEL_FETCHING_REPAYMENT_AMOUNT_ERROR("DME00006","Exception while fetching repayment amount using date"),
	DEBT_MODEL_TOTAL_INTEREST_ERROR("DME00007","Exception while computing total interest"),
	DEBT_MODEL_INTEREST_RATE_ERROR("DME00008","Exception while computing interest rate"),
	DEBT_MODEL_YEAR_FRAC_ERROR("DME00009","Exception while computing year frac"),
	DEBT_MODEL_INTEREST_ERROR("DME000010","Exception while computing interest"),
	DEBT_MODEL_DATE_INVALID("DME000011","Invalid prepayment dates"),
	UNKNOWN_EXCEPTION("UNKNOWN","Unknown Exception, Please contact System Admin"),
	;
	String code;
	String message;
	ErrorCodesAndMessages(String code,String message) {
		this.code=code;
		this.message=message;
	}
	public String getCode() {
		return this.code;
	}
	public String getMessage() {
		return this.message;
	}
	
}
