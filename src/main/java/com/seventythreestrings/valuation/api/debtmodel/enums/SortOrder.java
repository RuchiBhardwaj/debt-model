package com.seventythreestrings.valuation.api.debtmodel.enums;

public enum SortOrder {
    ASC("ASC"),
    DESC("DESC");

    private final String order;

    SortOrder(String order) {
        this.order = order;
    }

    public String getOrder() {
        return order;
    }
}
