package com.seventythreestrings.valuation.api.debtmodel.dto;

import com.seventythreestrings.valuation.api.debtmodel.model.AnnualHistoricalFinancial;
import com.seventythreestrings.valuation.api.debtmodel.model.AnnualProjectedFinancial;
import lombok.Data;

import java.util.List;

@Data
public class IssuerFinancialDto {

    private Long id;

    private double debtSeniorIssue;

    private double enterpriseValue;

    private List<AnnualHistoricalFinancial> annualHistoricalFinancials;

    private List<AnnualProjectedFinancial> annualProjectedFinancials;

    private int versionId;

    private Long debtModelId;
}
