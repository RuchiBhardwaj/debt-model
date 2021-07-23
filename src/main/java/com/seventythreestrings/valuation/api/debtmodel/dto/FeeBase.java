package com.seventythreestrings.valuation.api.debtmodel.dto;

public enum FeeBase {
    COMMITTED_CAPITAL("committed_capital"),
    CALLED_DOWN_CAPITAL("called_down_capital");

    private final String code;

    FeeBase(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
