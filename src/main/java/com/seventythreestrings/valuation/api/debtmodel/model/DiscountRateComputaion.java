package com.seventythreestrings.valuation.api.debtmodel.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.seventythreestrings.valuation.api.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "discount_rate_computation")
public class DiscountRateComputaion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "concluded_credit_spread_quarter1")
    private double concludedCreditSpreadQuarter1;

    @Column(name = "concluded_credit_spread_medium")
    private double concludedCreditSpreadMedium;

    @Column(name = "concluded_credit_spread_quarter3")
    private double concludedCreditSpreadQuarter3;

    @Column(name = "risk_free_rate_quarter1")
    private double riskFreeRateQuarter1;

    @Column(name = "risk_free_rate_medium")
    private double riskFreeRateMedium;

    @Column(name = "risk_free_rate_quarter3")
    private double riskFreeRateQuarter3;

    @Column(name = "ytm_quarter1")
    private double ytmQuarter1;

    @Column(name = "ytm_medium")
    private double ytmMedium;

    @Column(name = "ytm_quarter3")
    private double ytmQuarter3;


    @OneToMany(targetEntity=DiscountAdjustment.class, mappedBy="discountId",cascade=CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DiscountAdjustment> discountAdjustments = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "debt_model_id", nullable = false)
    private DebtModel debtModel;


}
