package com.seventythreestrings.valuation.api.debtmodel.dto;

public enum CustomizableCashflowType {
    INFLOW("inflow"),
    OUTFLOW("outflow");

    private final String code;

    CustomizableCashflowType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
