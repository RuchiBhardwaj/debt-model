package com.seventythreestrings.valuation.api.debtmodel.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.seventythreestrings.valuation.api.common.converter.LocalDateAttributeConverter;
import com.seventythreestrings.valuation.api.common.entity.BaseEntity;
import com.seventythreestrings.valuation.api.debtmodel.enums.InterestPayment;
import com.seventythreestrings.valuation.api.debtmodel.enums.InterestType;
import com.seventythreestrings.valuation.api.debtmodel.enums.PaymentFrequency;
import com.seventythreestrings.valuation.api.debtmodel.util.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "interest_details")
public class InterestDetails extends BaseEntity {
    public static final int END_OF_MONTH = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "interest_paid_or_accrued")
    @Enumerated(EnumType.STRING)
    private InterestType interestPaidOrAccrued;

    @Column(name = "interest_payment_type")
    @Enumerated(EnumType.STRING)
    private InterestPayment interestPaymentType;

    @Column(name = "first_interest_payment_date")
    @Convert(converter = LocalDateAttributeConverter.class)
    private LocalDate firstInterestPaymentDate;

    @Column(name = "regime_start_date")
    @Convert(converter = LocalDateAttributeConverter.class)
    private LocalDate regimeStartDate;

    @Column(name = "regime_end_date")
    @Convert(converter = LocalDateAttributeConverter.class)
    private LocalDate regimeEndDate;

    @Column(name = "version_id")
    private int versionId;

    @Column(name = "interest_payment_frequency")
    @Enumerated(EnumType.STRING)
    private PaymentFrequency interestPaymentFrequency;

    @Min(0)
    @Max(28)
    @Column(name = "interest_payment_day")
    private int interestPaymentDay;

    @Column(name = "base_rate_floor")
    private double baseRateFloor;

    @Column(name = "base_rate_cap")
    private double baseRateCap;

    @Column(name = "fixed_base_rate")
    private double fixedBaseRate = -1;

    @Column(name = "base_rate_spread")
    private double baseRateSpread;

    @ManyToOne
    @JoinColumn(name = "base_rate")
    private BaseRate baseRate;

    @ManyToOne
    @JoinColumn(name = "base_rate_curve")
    private BaseRateCurve baseRateCurve;

    @OneToOne
    @JoinColumn(name = "debt_model_id", nullable = false)
    private DebtModel debtModel;

    @JsonIgnore
    public Set<LocalDate> getCouponDates() {
        Set<LocalDate> couponDates = new HashSet<>();

        LocalDate firstInterestPaymentDate = this.getFirstInterestPaymentDate();
        LocalDate regimeEndDate = this.getRegimeEndDate();

        int dayOfPayment = this.getInterestPaymentDay();
        int monthIncrement = DateUtil.getMonthIncrementForPaymentFrequency(this.getInterestPaymentFrequency());

        LocalDate nextCouponDate = firstInterestPaymentDate;
        while (nextCouponDate.isBefore(regimeEndDate)) {
            couponDates.add(nextCouponDate);
            nextCouponDate = nextCouponDate.plusMonths(monthIncrement);
            nextCouponDate = nextCouponDate.withDayOfMonth(dayOfPayment == END_OF_MONTH ? nextCouponDate.lengthOfMonth() : dayOfPayment);
        }
        // add last payment date
        couponDates.add(regimeEndDate);

        return couponDates;
    }

    @JsonIgnore
    public double getDefaultBaseRateValue() {
        return 1.3;
    }

    @JsonIgnore
    public double getBaseRateValue() {
        if (fixedBaseRate == -1) {
            return getDefaultBaseRateValue();
        }

        return fixedBaseRate;
    }

    @JsonIgnore
    public double getTotalInterestRate() {
        return getBaseRateValue() + getBaseRateSpread();
    }
}
