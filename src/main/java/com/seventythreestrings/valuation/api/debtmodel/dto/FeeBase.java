package com.seventythreestrings.valuation.api.debtmodel.dto;

public enum FeeBase {
    COMMITTED_CAPITAL("committed_capital"),
    CALL_DOWN_CAPITAL("call_down_capital"),
    CUSTOM_AMOUNT("custom_amount");

    private final String code;

    FeeBase(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
