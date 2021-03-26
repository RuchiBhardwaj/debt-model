package com.seventythreestrings.valuation.api.exception;

public abstract class BaseException extends Exception {
	
	private static final long serialVersionUID = 5339422680384407966L;

	private ErrorCodesAndMessages errorCodesAndMessages;
	
	String code;
	String message;
	public BaseException(ErrorCodesAndMessages errorCodesAndMessages) {
		super(errorCodesAndMessages.getCode()+" : "+errorCodesAndMessages.getMessage());
		this.errorCodesAndMessages=errorCodesAndMessages;
	}
	public BaseException(ErrorCodesAndMessages errorCodesAndMessages, Throwable exception) {
		super(exception);
		this.errorCodesAndMessages=errorCodesAndMessages;
	}
	public String getCode() {
		return errorCodesAndMessages.getCode();
	}
	public String getAppErrorMessage() { 
		return errorCodesAndMessages.getMessage(); 
	}
	
	public ErrorCodesAndMessages getErrorCodesAndMessages() {
		return this.errorCodesAndMessages;
	}
}
