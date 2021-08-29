package com.seventythreestrings.valuation.api.debtmodel.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.seventythreestrings.valuation.api.debtmodel.enums.FeeBase;
import com.seventythreestrings.valuation.api.debtmodel.enums.PaymentFrequency;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDate;

@Data
public class DealFeesDto {
    private Long id;

    private FeeBase feeBase;

    private double annualFeePercentage;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate regimeStartDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate regimeEndDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate firstPaymentDate;

    @Min(0)
    @Max(28)
    private int dayOfPayment;

    private PaymentFrequency interestPaymentFrequency;

    private int versionId;

    private Long debtModelId;
}
