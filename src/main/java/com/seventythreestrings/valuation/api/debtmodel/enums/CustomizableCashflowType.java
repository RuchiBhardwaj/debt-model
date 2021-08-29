package com.seventythreestrings.valuation.api.debtmodel.enums;

public enum CustomizableCashflowType {
    INFLOW("Inflow"),
    OUTFLOW("Outflow");

    private final String code;

    CustomizableCashflowType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
