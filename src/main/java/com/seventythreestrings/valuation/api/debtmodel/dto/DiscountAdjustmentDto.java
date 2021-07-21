package com.seventythreestrings.valuation.api.debtmodel.dto;

import lombok.Data;


@Data
public class DiscountAdjustmentDto {

    private Long id;


    private String adjustmentName;


    private int quarter1;


    private int medium;

    private int quarter3;


    private Long discountRateComputationId;

}
