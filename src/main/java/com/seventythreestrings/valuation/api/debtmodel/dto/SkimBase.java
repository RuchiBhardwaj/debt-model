package com.seventythreestrings.valuation.api.debtmodel.dto;

public enum SkimBase {
    TERM_LOAN("term_loan"),
    REVOLVER("revolver");

    private final String code;

    SkimBase(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
