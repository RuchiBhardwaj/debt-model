package com.seventythreestrings.valuation.api.debtmodel.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CustomizableCashflowDto {

    private Long id;

    private String nameOfTheProperty;

    private CustomizableCashflowType cashflowType;

    private String cashflowPaymentMode;

    private CashflowDates cashflowDates;

    private LocalDate regimeStartDate;

    private LocalDate regimeEndDate;

    private LocalDate firstPaymentDate;

    private int dayOfPayment;

    private PaymentFrequency frequency;

    private CashflowAmount cashflowAmount;

    private double cashflowFixedAmount;

    private LocalDateTime dateSelection;

    private String cashflowComputationBase;

    private double cashflowPercentage;

    private double cashflowBaseCustomAmount;

    private Long debtModelId;

    private int versionId;

}
