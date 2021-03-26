package com.seventythreestrings.valuation.api.exception;

public class AppException extends BaseException {
	
	private static final long serialVersionUID = -4847976992884739596L;
	
	public AppException(ErrorCodesAndMessages errorCodesAndMessages) {
		super(errorCodesAndMessages);
	}

	public AppException(ErrorCodesAndMessages errorCodesAndMessages, Throwable exception) {
		super(errorCodesAndMessages, exception);
	}
	
	public static void newAppException(ErrorCodesAndMessages errorCodesAndMessages) throws AppException{
		throw new AppException(errorCodesAndMessages);
	}
	
	public static void newAppException(ErrorCodesAndMessages errorCodesAndMessages, Throwable exception) throws AppException{
		throw new AppException(errorCodesAndMessages,exception);
	}

}
