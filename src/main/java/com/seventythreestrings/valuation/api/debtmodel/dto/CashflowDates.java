package com.seventythreestrings.valuation.api.debtmodel.dto;

public enum CashflowDates {
    SPECIFIC_DATES("Specific Dates"),
    PRE_EXISTING_DATES("Pre-existing Dates"),
    EXCEL_DATES("Upload Excel for Dates");

    private String code;

    CashflowDates(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
