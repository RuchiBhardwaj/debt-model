package com.seventythreestrings.valuation.api.debtmodel.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.seventythreestrings.valuation.api.common.converter.LocalDateAttributeConverter;
import com.seventythreestrings.valuation.api.common.entity.BaseEntity;
import com.seventythreestrings.valuation.api.debtmodel.enums.DateType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "cashflow_schedule")
public class CashflowSchedule extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "from_date")
    @Convert(converter = LocalDateAttributeConverter.class)
    private LocalDate fromDate;

    @Column(name = "to_date")
    @Convert(converter = LocalDateAttributeConverter.class)
    private LocalDate toDate;

    @Column(name = "opening_principal_outstanding")
    private Double openingPrincipalOutstanding;

    @Column(name = "principal_inflow")
    private double principalInflow;

    @Column(name = "principal_repayment")
    private double principalRepayment;

    @Column(name = "call_premium_amount")
    private Double callPremiumAmount;

    @Column(name = "call_premium_rate")
    private Double callPremiumRate;

    @Column(name = "total_principal_outstanding")
    private double totalPrincipalOutstanding;

    @Column(name = "base_rate")
    private double baseRate;

    @Column(name = "base_rate_spread")
    private double baseRateSpread;

    @Column(name = "total_interest_rate")
    private double totalInterestRate;

    @Column(name = "interest_outflow")
    private double interestOutflow;

    @Column(name = "total_cash_movement")
    private double totalCashMovement;

    @Column(name = "partial_period")
    private double partialPeriod;

    @Column(name = "discounting_factor")
    private double discountingFactor;

    @Column(name = "annual_fee_percentage")
    private double annualFeePercentage;

    @Column(name = "deal_fees_outflow")
    private double dealFeesOutflow;

    @Column(name = "committed_capital")
    private Double committedCapital;

    @Column(name = "called_down_capital")
    private Double calledDownCapital;

    @Column(name = "undrawn_capital")
    private Double undrawnCapital;

    @Column(name = "interest_undrawn_percentage")
    private double interestUndrawnPercentage;

    @Column(name = "interest_undrawn_capital_outflow")
    private double interestUndrawnCapitalOutflow;

    @Column(name = "skim_percentage")
    private double skimPercentage;

    @Column(name = "skims_outflow")
    private double skimsOutflow;

    @Column(name = "present_value")
    private double presentValue;

    @Column(name = "date_type")
    @Enumerated(EnumType.STRING)
    private DateType dateType;

    @Transient
    private double yearFrac;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "cashflow_id")
    private Cashflow cashflow;

    public static CashflowSchedule fromPaymentSchedule(PaymentSchedule paymentSchedule) {
        CashflowSchedule cashflowSchedule = new CashflowSchedule();
        cashflowSchedule.setFromDate(paymentSchedule.getDate());
        cashflowSchedule.setToDate(paymentSchedule.getDate());
        cashflowSchedule.setPrincipalInflow(paymentSchedule.getAmount());

        return cashflowSchedule;
    }
}
