package com.seventythreestrings.valuation.api.debtmodel.model;

import com.seventythreestrings.valuation.api.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "annual_historical_financial")
public class AnnualHistoricalFinancial extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    @Column(name = "total_revenue")
    private double totalRevenue;


    @NotNull
    @Column(name = "cost_good_sold")
    private double costOfGoodSold;


    @NotNull
    @Column(name = "gross_profit")
    private double grossProfit;


    @NotNull
    @Column(name = "selling_general_administrative_expenses")
    private double sellingGeneralAdministrativeExpenses;


    @NotNull
    @Column(name = "advertising_promotion")
    private double advertisingPromotion;


    @NotNull
    @Column(name = "salaries_benefits_wages")
    private double salariesBenefitsWages;

    @NotNull
    @Column(name = "research_develop_cost")
    private double researchDevelopCost;

    @NotNull
    @Column(name = "other_expenses")
    private double otherExpenses;

    @NotNull
    @Column(name = "total_operating_expenses")
    private double totalOperatingExpenses;

    @NotNull
    @Column(name = "ebitda")
    private double ebitda;

    @NotNull
    @Column(name = "depreciation_amortization")
    private double depreciationAmortization;

    @NotNull
    @Column(name = "earning_before_interest_taxes")
    private double earningBeforeInterestTaxes;

    @NotNull
    @Column(name = "interest_expanse")
    private double interestExpanse;

    @NotNull
    @Column(name = "earning_before_taxes")
    private double earningBeforeTaxes;

    @NotNull
    @Column(name = "taxes")
    private double taxes;

    @NotNull
    @Column(name = "net_income")
    private double netIncome;

    @NotNull
    @Column(name = "assets")
    private double assets;

    @NotNull
    @Column(name = "cash")
    private double cash;

    @NotNull
    @Column(name = "account_receivable")
    private double accountReceivable;

    @NotNull
    @Column(name = "inventory")
    private double inventory;

    @NotNull
    @Column(name = "total_current_assets")
    private double totalCurrentAssets;

    @NotNull
    @Column(name = "total_fixed_assets")
    private double totalFixedAssets;

    @NotNull
    @Column(name = "total_assets")
    private double totalAssets;

    @NotNull
    @Column(name = "account_payable")
    private double accountPayable;

    @NotNull
    @Column(name = "short_term_debt")
    private double shortTermDebt;

    @NotNull
    @Column(name = "total_current_liabilities")
    private double totalCurrentLiabilities;

    @NotNull
    @Column(name = "long_term_debt")
    private double longTermDebt;

    @NotNull
    @Column(name = "total_non_current_liabilities")
    private double totalNonCurrentLiabilities;

    @NotNull
    @Column(name = "total_liabilities")
    private double totalLiabilities;

    @NotNull
    @Column(name = "total_shareholders_equity")
    private double totalShareholdersEquity;

    @NotNull
    @Column(name = "total_liabilities_equity")
    private double totalLiabilitiesEquity;

    @NotNull
    @Column(name = "cash_flow_operation")
    private double cashFlowOperation;

    @NotNull
    @Column(name = "capital_expenditure")
    private double capitalExpenditure;

    @NotNull
    @Column(name = "cash_from_investing")
    private double cashFromInvesting;

    @NotNull
    @Column(name = "cash_from_financing")
    private double cashFromFinancing;

    @NotNull
    @Column(name = "closing_cash_balance")
    private double closingCahsBalance;

    @NotNull
    @Column(name = "year")
    private int year;







}
