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


    @Column(name = "total_revenue")
    private double totalRevenue;



    @Column(name = "cost_good_sold")
    private double costOfGoodSold;



    @Column(name = "gross_profit")
    private double grossProfit;



    @Column(name = "selling_general_administrative_expenses")
    private double sellingGeneralAdministrativeExpenses;



    @Column(name = "advertising_promotion")
    private double advertisingPromotion;



    @Column(name = "salaries_benefits_wages")
    private double salariesBenefitsWages;


    @Column(name = "research_develop_cost")
    private double researchDevelopCost;


    @Column(name = "other_expenses")
    private double otherExpenses;


    @Column(name = "total_operating_expenses")
    private double totalOperatingExpenses;


    @Column(name = "ebitda")
    private double ebitda;

    @NotNull
    @Column(name = "depreciation_amortization")
    private double depreciationAmortization;


    @Column(name = "earning_before_interest_taxes")
    private double earningBeforeInterestTaxes;


    @Column(name = "interest_expanse")
    private double interestExpanse;


    @Column(name = "earning_before_taxes")
    private double earningBeforeTaxes;


    @Column(name = "taxes")
    private double taxes;


    @Column(name = "net_income")
    private double netIncome;


    @Column(name = "assets")
    private double assets;

    @Column(name = "cash")
    private double cash;


    @Column(name = "account_receivable")
    private double accountReceivable;


    @Column(name = "inventory")
    private double inventory;


    @Column(name = "total_current_assets")
    private double totalCurrentAssets;


    @Column(name = "total_fixed_assets")
    private double totalFixedAssets;


    @Column(name = "total_assets")
    private double totalAssets;


    @Column(name = "account_payable")
    private double accountPayable;


    @Column(name = "short_term_debt")
    private double shortTermDebt;


    @Column(name = "total_current_liabilities")
    private double totalCurrentLiabilities;


    @Column(name = "long_term_debt")
    private double longTermDebt;


    @Column(name = "total_non_current_liabilities")
    private double totalNonCurrentLiabilities;


    @Column(name = "total_liabilities")
    private double totalLiabilities;


    @Column(name = "total_shareholders_equity")
    private double totalShareholdersEquity;


    @Column(name = "total_liabilities_equity")
    private double totalLiabilitiesEquity;


    @Column(name = "cash_flow_operation")
    private double cashFlowOperation;


    @Column(name = "capital_expenditure")
    private double capitalExpenditure;


    @Column(name = "cash_from_investing")
    private double cashFromInvesting;


    @Column(name = "cash_from_financing")
    private double cashFromFinancing;


    @Column(name = "closing_cash_balance")
    private double closingCashBalance;

    @Column(name = "year")
    private String year;







}
