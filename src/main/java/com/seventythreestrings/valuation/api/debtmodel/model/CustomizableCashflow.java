package com.seventythreestrings.valuation.api.debtmodel.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.seventythreestrings.valuation.api.common.entity.BaseEntity;
import com.seventythreestrings.valuation.api.debtmodel.dto.CashflowAmount;
import com.seventythreestrings.valuation.api.debtmodel.dto.CashflowDates;
import com.seventythreestrings.valuation.api.debtmodel.dto.CustomizableCashflowType;
import com.seventythreestrings.valuation.api.debtmodel.dto.PaymentFrequency;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "cutomizable_cashflow")
public class CustomizableCashflow extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name_of_the_property")
    private String nameOfTheProperty;

    @Column(name = "cashflow_type")
    private CustomizableCashflowType cashflowType;

    @Column(name = "cashflow_payment_mode")
    private String cashflowPaymentMode;

    @Column(name = "cashflow_dates")
    private CashflowDates cashflowDates;

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

    @Column(name = "frequency")
    private PaymentFrequency frequency;

    @Column(name = "cashflow_amount")
    private CashflowAmount cashflowAmount;

    @Column(name = "cashflow_fixed_amount")
    private double cashflowFixedAmount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "UTC")
    @Column(name = "date_selection")
    private LocalDateTime dateSelection;

    @Column(name = "cashflow_computation_base")
    private String cashflowComputationBase;

    @Column(name = "cashflow_percentage")
    private double cashflowPercentage;

    @Column(name = "cashflow_base_custom_amount")
    private double cashflowBaseCustomAmount;

    @OneToOne
    @JoinColumn(name = "debt_model_id", nullable = false)
    private DebtModel debtModel;

    @Column(name = "version_id")
    private int versionId;


}