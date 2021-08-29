package com.seventythreestrings.valuation.api.debtmodel.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class LookUpDebtDetailsDto {

    private Long debtId;

    private String companyName;

    private UUID companyId;

    private UUID fundId;

    private String fundName;
}
