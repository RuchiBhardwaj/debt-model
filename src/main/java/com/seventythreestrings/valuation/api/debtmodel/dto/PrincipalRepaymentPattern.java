package com.seventythreestrings.valuation.api.debtmodel.dto;

public enum PrincipalRepaymentPattern {
    HUNDRED_PCT_BULLET_PAYMENT_AT_THE_END("100% Bullet Payment at the end"),
    INTERIM_FIXED_EQUAL_PAYMENTS("Interim Fixed Equal Payments"),
    INTERIM_VARIABLE_PAYMENTS("Interim Variable Payments");

    private final String code;

    PrincipalRepaymentPattern(String s) {
        code = s;
    }

    public String getCode() {
        return code;
    }
}
