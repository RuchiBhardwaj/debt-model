package com.seventythreestrings.valuation.api.debtmodel.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.seventythreestrings.valuation.api.common.entity.BaseEntity;
import com.seventythreestrings.valuation.api.debtmodel.dto.PaymentFrequency;
import com.seventythreestrings.valuation.api.debtmodel.dto.SkimBase;
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
@Table(name = "skims")
public class Skims extends BaseEntity {
    public static final int END_OF_MONTH = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "skim_percentage")
    private double skimPercentage;

    @Column(name = "outstanding_balance_of_valuation_date")
    private double outstandingBalance;

    @Column(name = "skim_base")
    private SkimBase skimBase;

    @Column(name = "regime_start_date")
    private LocalDate regimeStartDate;

    @Column(name = "regime_end_date")
    private LocalDate regimeEndDate;

    @Column(name = "first_payment_date")
    private LocalDate firstPaymentDate;

    @Min(0)
    @Max(28)
    @Column(name = "day_of_payment")
    private int dayOfPayment;

    @Column(name = "skim_payment_frequency")
    private PaymentFrequency skimPaymentFrequency;

    @Column(name = "version_id")
    private int versionId;

    @OneToOne
    @JoinColumn(name = "debt_model_id", nullable = false)
    private DebtModel debtModel;

    @JsonIgnore
    public Set<LocalDate> getCouponDates() {
        Set<LocalDate> couponDates = new HashSet<>();

        LocalDate firstPaymentDate = this.getFirstPaymentDate();
        LocalDate regimeEndDate = this.getRegimeEndDate();

        int dayOfPayment = this.getDayOfPayment();
        int monthIncrement = DateUtil.getMonthIncrementForPaymentFrequency(this.getSkimPaymentFrequency());

        LocalDate nextCouponDate = firstPaymentDate;
        while (nextCouponDate.isBefore(regimeEndDate)) {
            couponDates.add(nextCouponDate);
            nextCouponDate = nextCouponDate.plusMonths(monthIncrement);
            nextCouponDate = nextCouponDate.withDayOfMonth(dayOfPayment == END_OF_MONTH ? nextCouponDate.lengthOfMonth() : dayOfPayment);
        }
        // add last payment date
        couponDates.add(regimeEndDate);

        return couponDates;
    }
}
