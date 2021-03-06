package com.seventythreestrings.valuation.api.debtmodel.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.seventythreestrings.valuation.api.debtmodel.enums.DateType;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class CashflowScheduleDto {
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fromDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate toDate;

    private Double openingPrincipalOutstanding;

    private double principalInflow;

    private double principalRepayment;

    private Double callPremiumAmount;

    private Double callPremiumRate;

    private double totalPrincipalOutstanding;

    private double baseRate;

    private double baseRateSpread;

    private double totalInterestRate;

    private double interestOutflow;

    private double totalCashMovement;

    private double partialPeriod;

    private double discountingFactor;

    private double annualFeePercentage;

    private double dealFeesOutflow;

    private Double committedCapital;

    private Double calledDownCapital;

    private Double undrawnCapital;

    private double interestUndrawnPercentage;

    private double interestUndrwanCapitalOutflow;

    private double skimPercentage;

    private double skimsOutflow;

    private String customCashflowName;

    private Double customCashflowPercentage;

    private Double customCashflowAmount;

    private double presentValue;

    private DateType dateType;

    private double yearFrac;
}
