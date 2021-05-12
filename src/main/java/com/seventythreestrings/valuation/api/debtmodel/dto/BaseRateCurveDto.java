package com.seventythreestrings.valuation.api.debtmodel.dto;

import lombok.Data;

@Data
public class BaseRateCurveDto {
    private Long id;

    private String name;

    private Long baseRateId;
}
