package com.seventythreestrings.valuation.api.debtmodel.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class InterimPaymentDetailsDto {

    private Long id;


    private LocalDate date;


    private double amount;

    private Long customizableCashflowExcelId;
}
