package com.seventythreestrings.valuation.api.debtmodel.dto;

import lombok.Data;

import java.util.List;

@Data
public class CustomizableCashflowExcelDDto {

    private Long id;

    private String nameOfTheProperty;

    private CustomizableCashflowType cashflowType;

    private CashflowDates cashflowDates;

    private Long debtModelId;

    private int versionId;

    private List<InterimPaymentDetailsDto> interimPaymentDetails;
}
