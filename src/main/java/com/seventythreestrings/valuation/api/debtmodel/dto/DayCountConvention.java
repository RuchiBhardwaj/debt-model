package com.seventythreestrings.valuation.api.debtmodel.dto;

public enum DayCountConvention {
    US_30_BY_360(0),
    ACTUAL_BY_ACTUAL(1),
    ACTUAL_BY_360(2),
    ACTUAL_BY_365(3),
    EUR_30_BY_360(4),
    ;

    private final int basis;

    DayCountConvention(int i) {
        basis = i;
    }

    public int getBasis() {
        return basis;
    }
}
