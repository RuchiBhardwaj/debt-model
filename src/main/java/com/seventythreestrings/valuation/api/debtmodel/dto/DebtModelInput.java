package com.seventythreestrings.valuation.api.debtmodel.dto;

public enum DebtModelInput {
    GENERAL_DETAILS("general_details"),
    INTEREST_DETAILS("interest_details"),
    PREPAYMENT_DETAILS("prepayment_details"),
    DEAL_FEES("deal_fees"),
    INTEREST_UNDRAWN_CAPITAL("interest_undrawn_capital"),
    SKIMS("skim"),
    CALL_PREMIUM("call_premium"),
    ;

    private final String code;

    DebtModelInput(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
