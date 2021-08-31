package com.seventythreestrings.valuation.api.debtmodel.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.seventythreestrings.valuation.api.common.converter.LocalDateAttributeConverter;
import com.seventythreestrings.valuation.api.common.entity.BaseEntity;
import com.seventythreestrings.valuation.api.debtmodel.enums.*;
import com.seventythreestrings.valuation.api.debtmodel.util.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "customizable_cashflow")
public class CustomizableCashflow extends BaseEntity {
    public static final int END_OF_MONTH = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "name_of_the_property")
    private String nameOfTheProperty;

    @Column(name = "cashflow_type")
    @Enumerated(EnumType.STRING)
    private CustomizableCashflowType cashflowType;

    @Column(name = "cashflow_payment_mode")
    @Enumerated(EnumType.STRING)
    private InterestType cashflowPaymentMode;

    @Column(name = "cashflow_dates")
    @Enumerated(EnumType.STRING)
    private CashflowDates cashflowDates;

    @Column(name = "regime_start_date")
    @Convert(converter = LocalDateAttributeConverter.class)
    private LocalDate regimeStartDate;

    @Column(name = "regime_end_date")
    @Convert(converter = LocalDateAttributeConverter.class)
    private LocalDate regimeEndDate;

    @Column(name = "first_payment_date")
    @Convert(converter = LocalDateAttributeConverter.class)
    private LocalDate firstPaymentDate;

    @Min(0)
    @Max(28)
    @Column(name = "day_of_payment")
    private int dayOfPayment;

    @Column(name = "frequency")
    @Enumerated(EnumType.STRING)
    private PaymentFrequency frequency;

    @Column(name = "cashflow_amount")
    @Enumerated(EnumType.STRING)
    private CashflowAmount cashflowAmount;

    @Column(name = "cashflow_fixed_amount")
    private double cashflowFixedAmount;

    @Column(name = "date_selection", columnDefinition = "DATE")
    @Convert(converter = LocalDateAttributeConverter.class)
    private LocalDate dateSelection;

    @Column(name = "cashflow_computation_base")
    @Enumerated(EnumType.STRING)
    private FeeBase cashflowComputationBase;

    @Column(name = "cashflow_percentage")
    private double cashflowPercentage;

    @Column(name = "cashflow_base_custom_amount")
    private double cashflowBaseCustomAmount;

    @OneToOne
    @JoinColumn(name = "debt_model_id", nullable = false)
    private DebtModel debtModel;

    @Column(name = "version_id")
    private int versionId;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @JsonIgnore
    public Set<LocalDate> getCouponDates() {
        Set<LocalDate> couponDates = new HashSet<>();

        if (this.getCashflowDates() == CashflowDates.SPECIFIC_DATES) {
            LocalDate firstInterestPaymentDate = this.getFirstPaymentDate();
            LocalDate regimeEndDate = this.getRegimeEndDate();

            int dayOfPayment = this.getDayOfPayment();
            int monthIncrement = DateUtil.getMonthIncrementForPaymentFrequency(this.getFrequency());

            LocalDate nextCouponDate = firstInterestPaymentDate;
            while (nextCouponDate.isBefore(regimeEndDate)) {
                couponDates.add(nextCouponDate);
                nextCouponDate = nextCouponDate.plusMonths(monthIncrement);
                nextCouponDate = nextCouponDate.withDayOfMonth(dayOfPayment == END_OF_MONTH ? nextCouponDate.lengthOfMonth() : dayOfPayment);
            }
            // add last payment date
            couponDates.add(regimeEndDate);
        }

        return couponDates;
    }
}
