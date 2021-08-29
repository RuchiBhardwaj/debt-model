package com.seventythreestrings.valuation.api.debtmodel.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.seventythreestrings.valuation.api.debtmodel.enums.CashflowAmount;
import com.seventythreestrings.valuation.api.debtmodel.enums.PaymentFrequency;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class CustomizableCashflowDto extends BaseCustomizableCashflowDto {
    private String cashflowPaymentMode;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate regimeStartDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate regimeEndDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate firstPaymentDate;

    private int dayOfPayment;

    private PaymentFrequency frequency;

    private CashflowAmount cashflowAmount;

    private double cashflowFixedAmount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateSelection;

    private String cashflowComputationBase;

    private double cashflowPercentage;

    private double cashflowBaseCustomAmount;
}
