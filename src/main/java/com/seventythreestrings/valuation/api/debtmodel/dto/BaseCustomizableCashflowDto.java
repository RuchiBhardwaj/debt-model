package com.seventythreestrings.valuation.api.debtmodel.dto;

import com.seventythreestrings.valuation.api.debtmodel.enums.CashflowDates;
import com.seventythreestrings.valuation.api.debtmodel.enums.CustomizableCashflowType;
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
