package com.seventythreestrings.valuation.api.debtmodel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CompanyDetailsResponseDto {

    private Object details;

    private String companyName;

    private UUID companyId;
}
