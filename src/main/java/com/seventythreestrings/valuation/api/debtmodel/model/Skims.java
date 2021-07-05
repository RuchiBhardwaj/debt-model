package com.seventythreestrings.valuation.api.debtmodel.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.seventythreestrings.valuation.api.common.entity.BaseEntity;
import com.seventythreestrings.valuation.api.debtmodel.dto.PaymentFrequency;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "skims")
public class Skims extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "skim_percentage")
    private double skimPercentage;

    @Column(name = "outstanding_Balance_Of_ValuationDate")
    private double Balance;

    @Column(name = "skim_base")
    private int skimBase;

    @Column(name = "regime_start_date")
    private LocalDate regimeStartDate;

    @Column(name = "regime_end_date")
    private LocalDate regimeEndDate;

    @Column(name = "first_payment_date")
    private LocalDate firstPaymentDate;

    @Column(name = "day_of_payment_date")
    private LocalDate dayOfPaymentDate;

    @Column(name = "skim_payment_frequency")
    private PaymentFrequency skimPaymentFrequency;

    @OneToOne
    @JoinColumn(name = "debt_model_id", nullable = false)
    private DebtModel debtModel;

}
