package com.seventythreestrings.valuation.api.debtmodel.dto;


import lombok.Data;

@Data
public class AnnualHistoricalFinancialDto {

    private Long id;

    private double totalRevenue;

    private double costOfGoodSold;

    private double grossProfit;

    private double sellingGeneralAdministrativeExpenses;

    private double advertisingPromotion;

    private double salariesBenefitsWages;

    private double researchDevelopCost;

    private double otherExpenses;

    private double totalOperatingExpenses;

    private double ebitda;

    private double depreciationAmortization;

    private double earningBeforeInterestTaxes;

    private double interestExpanse;

    private double earningBeforeTaxes;

    private double taxes;

    private double netIncome;

    private double assets;

    private double cash;

    private double accountReceivable;

    private double inventory;

    private double totalCurrentAssets;

    private double totalFixedAssets;

    private double totalAssets;

    private double accountPayable;

    private double shortTermDebt;

    private double totalCurrentLiabilities;

    private double longTermDebt;

    private double totalNonCurrentLiabilities;

    private double totalLiabilities;

    private double totalShareholdersEquity;

    private double totalLiabilitiesEquity;

    private double cashFlowOperation;

    private double capitalExpenditure;

    private double cashFromInvesting;

    private double cashFromFinancing;

    private double closingCashBalance;

    private String year;

    private Long issuerFinancialId;
}
