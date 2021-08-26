package com.seventythreestrings.valuation.api.debtmodel.dto;

import lombok.Data;

@Data
public class BaseCustomizableCashflowDto {
    private Long id;

    private String nameOfTheProperty;

    private CustomizableCashflowType cashflowType;

    private CashflowDates cashflowDates;

    private Long debtModelId;

    private int versionId;

    private Integer sortOrder;
}
