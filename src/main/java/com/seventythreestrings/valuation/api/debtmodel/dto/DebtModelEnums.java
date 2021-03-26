package com.seventythreestrings.valuation.api.debtmodel.dto;


public class DebtModelEnums {
	
	public enum FrequencyOfPaymentType{
		
	MONTHLY("monthly"),
	QUARTERLY("quarterly"),
	SEMI_ANNUAL("semiannual"),
	ANNUAL("annual");	
		
		private String code;

		FrequencyOfPaymentType(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}
	    
	}

	
	
	}
