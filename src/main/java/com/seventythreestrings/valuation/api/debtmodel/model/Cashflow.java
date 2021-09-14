package com.seventythreestrings.valuation.api.debtmodel.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.seventythreestrings.valuation.api.common.converter.LocalDateAttributeConverter;
import com.seventythreestrings.valuation.api.common.entity.BaseEntity;
import com.seventythreestrings.valuation.api.debtmodel.enums.DayCountConvention;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "cashflow")
public class Cashflow extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "origination_date")
    @Convert(converter = LocalDateAttributeConverter.class)
    private LocalDate originationDate;

    @Column(name = "valuation_date")
    @Convert(converter = LocalDateAttributeConverter.class)
    private LocalDate valuationDate;

    @Column(name = "exit_date")
    @Convert(converter = LocalDateAttributeConverter.class)
    private LocalDate exitDate;

    @Column(name = "maturity_date")
    @Convert(converter = LocalDateAttributeConverter.class)
    private LocalDate maturityDate;

    @Column(name = "discount_rate")
    private double discountRate;

    @Column(name = "present_value_sum")
    private double presentValueSum;

    @Column(name = "percentage_par")
    private double percentagePar;

    @Column(name = "present_value_sum_exit")
    private Double presentValueSumExit;

    @Column(name = "percentage_par_exit")
    private Double percentageParExit;

    @Column(name = "internal_rate_of_return")
    private double internalRateOfReturn;

    @Column(name = "day_count_convention")
    private DayCountConvention dayCountConvention;

    @Column(name = "version_id")
    private int versionId;

    @OneToMany(mappedBy = "cashflow", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("fromDate ASC")
    private Set<CashflowSchedule> schedules = new LinkedHashSet<>();

    @OneToOne
    @JoinColumn(name = "debt_model_id", nullable = false)
    private DebtModel debtModel;
}
