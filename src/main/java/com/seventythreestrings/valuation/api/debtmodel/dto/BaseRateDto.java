package com.seventythreestrings.valuation.api.debtmodel.dto;

import lombok.Data;

@Data
public class BaseRateDto {
    private Long id;

    private String name;

    private boolean isFixed;
}
