package com.seventythreestrings.valuation.api.debtmodel.dto;

import lombok.Data;

@Data
public class DiscountAdjustmentDto {

    private Long id;

    private String adjustmentName;

    private double quartile1;

    private double median;

    private double quartile3;

    private Long discountRateComputationId;
}
