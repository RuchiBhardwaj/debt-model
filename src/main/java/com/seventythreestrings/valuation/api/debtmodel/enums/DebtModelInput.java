package com.seventythreestrings.valuation.api.debtmodel.enums;

public enum DebtModelInput {
    GENERAL_DETAILS("general_details"),
    INTEREST_DETAILS("interest_details"),
    REPAYMENT_DETAILS("prepayment_details"),
    CALL_PREMIUM("call_premium"),
    DEAL_FEES("deal_fees"),
    INTEREST_UNDRAWN_CAPITAL("interest_undrawn_capital"),
    ISSUER_FINANCIAL("issuer_financial"),
    SKIMS("skims"),
    CUSTOMIZABLE_CASHFLOW("customizable_cashflow")
    ;

    private final String code;

    DebtModelInput(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
