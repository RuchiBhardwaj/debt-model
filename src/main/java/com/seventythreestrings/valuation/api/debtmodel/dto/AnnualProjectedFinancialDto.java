package com.seventythreestrings.valuation.api.debtmodel.dto;

import lombok.Data;

@Data
public class AnnualProjectedFinancialDto {


    private Long id;


    private double Revenue;

    private double costOfGoodSold;

    private double grossProfit;

    private double grossProfitMargin;

    private double sellingGeneralAdmin;

    private double employeeBenefit;

    private double researchDevelopment;

    private double marketingExpenses;

    private double otherExpenses;

    private double ebitda;

    private double depreciationAmortization;

    private double interestPayment;

    private double totalTaxableIncome;

    private double taxPayment;

    private double netProfit;

    private double accountReceivableAmount;

    private double inventory;

    private double accountsPayable;

    private double totalDebt;

    private double principal_repayment;

    private double capitalExpenditure;

    private double equityFundraisingPlans;

    private double totalShareholdersEquity;

    private int year;

    private Long issuerFinancialId;
}
