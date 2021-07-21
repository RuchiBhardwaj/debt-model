package com.seventythreestrings.valuation.api.debtmodel.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.seventythreestrings.valuation.api.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "discount_adjustment")
public class DiscountAdjustment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "adjustment_name")
    private String adjustmentName;

    @Column(name = "quarter1")
    private int quarter1;

    @Column(name = "medium")
    private int medium;

    @Column(name = "quarter3")
    private int quarter3;

    @ManyToOne
    @JoinColumn(name = "discount_adjustment", nullable = false)
    private DiscountRateComputaion discountRateComputation;


}
