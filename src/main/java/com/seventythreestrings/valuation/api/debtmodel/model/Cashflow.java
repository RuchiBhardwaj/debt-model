package com.seventythreestrings.valuation.api.debtmodel.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.seventythreestrings.valuation.api.common.entity.BaseEntity;
import com.seventythreestrings.valuation.api.debtmodel.dto.DayCountConvention;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "origination_date")
    private LocalDate originationDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "valuation_date")
    private LocalDate valuationDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "exit_date")
    private LocalDate exitDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "maturity_date")
    private LocalDate maturityDate;

    @Column(name = "discount_rate")
    private double discountRate;

    @Column(name = "present_value_sum")
    private double presentValueSum;

    @Column(name = "percentage_par")
    private double percentagePar;

    @Column(name = "present_value_sum_exit")
    private double presentValueSumExit;

    @Column(name = "percentage_par_exit")
    private double percentageParExit;

    @Column(name = "internal_rate_of_return")
    private double internalRateOfReturn;

    @Column(name = "day_count_convention")
    private DayCountConvention dayCountConvention;

    @OneToMany(mappedBy="cashflow", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("fromDate ASC")
    private Set<CashflowSchedule> schedules = new LinkedHashSet<>();

    @OneToOne
    @JoinColumn(name = "debt_model_id", nullable = false)
    private DebtModel debtModel;
}
