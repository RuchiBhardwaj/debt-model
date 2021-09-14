package com.seventythreestrings.valuation.api.debtmodel.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.seventythreestrings.valuation.api.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "discount_rate_computation")
public class DiscountRateComputaion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "concluded_credit_spread_quartile1")
    private double concludedCreditSpreadQuartile1;

    @Column(name = "concluded_credit_spread_median")
    private double concludedCreditSpreadMedian;

    @Column(name = "concluded_credit_spread_quartile3")
    private double concludedCreditSpreadQuartile3;

    @Column(name = "risk_free_rate_quartile1")
    private double riskFreeRateQuartile1;

    @Column(name = "risk_free_rate_median")
    private double riskFreeRateMedian;

    @Column(name = "risk_free_rate_quartile3")
    private double riskFreeRateQuartile3;

    @Column(name = "ytm_quartile1")
    private double ytmQuartile1;

    @Column(name = "ytm_median")
    private double ytmMedian;

    @Column(name = "ytm_quartile3")
    private double ytmQuartile3;

    @Column(name = "version_id")
    private int versionId;

    @OneToMany(mappedBy="discountRateComputation", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DiscountAdjustment> discountAdjustments = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "debt_model_id", nullable = false)
    private DebtModel debtModel;
}
