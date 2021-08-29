package com.seventythreestrings.valuation.api.debtmodel.enums;

public enum PaymentFrequency {

    MONTHLY("monthly"),
    QUARTERLY("quarterly"),
    SEMI_ANNUAL("semiannual"),
    ANNUAL("annual");

    private String code;

    PaymentFrequency(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
