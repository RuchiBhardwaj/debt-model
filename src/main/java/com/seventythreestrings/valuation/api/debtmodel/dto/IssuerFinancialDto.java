package com.seventythreestrings.valuation.api.debtmodel.dto;

import lombok.Data;

@Data
public class IssuerFinancialDto {

    private Long id;

    private double debtSeniorIssue;

    private double enterpriseValue;

    private Long annualHistoricalFinancialId;

    private Long annualProjectedFinancialId;

    private Long debtModelId;
}
