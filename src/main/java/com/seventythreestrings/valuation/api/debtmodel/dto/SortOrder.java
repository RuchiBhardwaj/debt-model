package com.seventythreestrings.valuation.api.debtmodel.dto;

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
