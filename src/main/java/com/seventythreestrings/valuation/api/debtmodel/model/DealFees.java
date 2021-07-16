package com.seventythreestrings.valuation.api.debtmodel.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.seventythreestrings.valuation.api.common.entity.BaseEntity;
import com.seventythreestrings.valuation.api.debtmodel.dto.PaymentFrequency;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "deal_fees")
public class DealFees extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fee_base")
    private int feeBase;

    @Column(name = "annual_fee_percentage")
    private double annualFeePercentage;

    @Column(name = "regime_start_date")
    private LocalDate regimeStartDate;

    @Column(name = "regime_end_date")
    private LocalDate regimeEndDate;

    @Column(name = "first_payment_date")
    private LocalDate firstPaymentDate;

    @Column(name = "day_of_payment_date")
    private LocalDate dayOfPaymentDate;

    @Column(name = "interest_payment_frequency")
    private PaymentFrequency interestPaymentFrequency;


    @Column(name = "version_id")
    private int versionId;

    @OneToOne
    @JoinColumn(name = "debt_model_id", nullable = false)
    private DebtModel debtModel;


}
