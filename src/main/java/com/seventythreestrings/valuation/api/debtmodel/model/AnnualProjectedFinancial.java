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
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;


    @Column(name = "revenue")
    private double Revenue;


    @Column(name = "cost_of_good_sold")
    private double costOfGoodSold;

    @NotNull
    @Column(name = "gross_profit")
    private double grossProfit;


    @Column(name = "gross_profit_margin")
    private double grossProfitMargin;

    @NotNull
    @Column(name = "selling_general_admin")
    private double sellingGeneralAdmin;


    @Column(name = "employee_benefit")
    private double employeeBenefit;

    @NotNull
    @Column(name = "research_development")
    private double researchDevelopment;


    @Column(name = "marketing_expenses")
    private double marketingExpenses;

    @NotNull
    @Column(name = "other_expenses")
    private double otherExpenses;


    @Column(name = "ebitda")
    private double ebitda;


    @Column(name = "depreciation_amortization")
    private double depreciationAmortization;


    @Column(name = "interest_payment")
    private double interestPayment;


    @Column(name = "total_taxable_income")
    private double totalTaxableIncome;


    @Column(name = "tax_payment")
    private double taxPayment;


    @Column(name = "net_profit")
    private double netProfit;


    @Column(name = "account_receivable_amount")
    private double accountReceivableAmount;


    @Column(name = "inventory")
    private double inventory;


    @Column(name = "accounts_payable")
    private double accountsPayable;


    @Column(name = "total_debt")
    private double totalDebt;


    @Column(name = "principal_repayment")
    private double principal_repayment;


    @Column(name = "capital_expenditure")
    private double capitalExpenditure;


    @Column(name = "equity_fundraising_plans")
    private double equityFundraisingPlans;

    @Column(name = "total_shareholders_equity")
    private double totalShareholdersEquity;


    @Column(name = "year")
    private int year;

    @ManyToOne
    @JoinColumn(name = "issuer_financial", nullable = false)
    private IssuerFinancial issuerFinancial;

}
