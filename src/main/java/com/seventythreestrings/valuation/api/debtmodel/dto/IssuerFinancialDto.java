package com.seventythreestrings.valuation.api.debtmodel.dto;

import lombok.Data;

import java.util.List;

@Data
public class IssuerFinancialDto {

    private Long id;

    private double debtSeniorIssue;

    private double enterpriseValue;

    private List<AnnualHistoricalFinancialDto> annualHistoricalFinancials;

    private List<AnnualProjectedFinancialDto> annualProjectedFinancials;

    private int versionId;

    private Long debtModelId;
}
