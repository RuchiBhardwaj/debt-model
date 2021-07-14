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
@Table(name = "annual_projected_financial")
public class AnnualProjectedFinancial extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "revenue")
    private double Revenue;

    @NotNull
    @Column(name = "cost_of_good_sold")
    private double costOfGoodSold;

    @NotNull
    @Column(name = "gross_profit")
    private double grossProfit;

    @NotNull
    @Column(name = "gross_profit_margin")
    private double grossProfitMargin;

    @NotNull
    @Column(name = "selling_general_admin")
    private double sellingGeneralAdmin;

    @NotNull
    @Column(name = "employee_benefit")
    private double employeeBenefit;

    @NotNull
    @Column(name = "research_development")
    private double researchDevelopment;

    @NotNull
    @Column(name = "marketing_expenses")
    private double marketingExpenses;

    @NotNull
    @Column(name = "other_expenses")
    private double otherExpenses;

    @NotNull
    @Column(name = "ebitda")
    private double EBITDA;

    @NotNull
    @Column(name = "depreciation_amortization")
    private double depreciationAmortization;

    @NotNull
    @Column(name = "interest_payment")
    private double interestPayment;

    @NotNull
    @Column(name = "total_taxable_income")
    private double totalTaxableIncome;

    @NotNull
    @Column(name = "tax_payment")
    private double taxPayment;

    @NotNull
    @Column(name = "net_profit")
    private double netProfit;

    @NotNull
    @Column(name = "account_receivable_amount")
    private double accountReceivableAmount;

    @NotNull
    @Column(name = "inventory")
    private double Inventory;

    @NotNull
    @Column(name = "accounts_payable")
    private double accountsPayable;

    @NotNull
    @Column(name = "total_debt")
    private double totalDebt;

    @NotNull
    @Column(name = "principal_repayment")
    private double principal_repayment;

    @NotNull
    @Column(name = "capital_expenditure")
    private double capitalExpenditure;

    @NotNull
    @Column(name = "equity_fundraising_plans")
    private double equityFundraisingPlans;

    @NotNull
    @Column(name = "total_shareholders_equity")
    private double totalShareholdersEquity;

    @NotNull
    @Column(name = "year")
    private int year;


}
