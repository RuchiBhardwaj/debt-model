package com.seventythreestrings.valuation.api.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse<T> {
	private T response;
	private boolean success=true;
	private String message;
	private String errorCode;
}
