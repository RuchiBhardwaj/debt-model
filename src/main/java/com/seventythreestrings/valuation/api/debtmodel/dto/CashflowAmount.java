package com.seventythreestrings.valuation.api.debtmodel.dto;

public enum CashflowAmount {
    PERCENTAGE("Percentage"),
    FIXED_AMOUNT("Fixed Amount");

    private final String code;

    CashflowAmount(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
