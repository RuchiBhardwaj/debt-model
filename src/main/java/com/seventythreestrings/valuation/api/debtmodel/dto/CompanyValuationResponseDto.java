package com.seventythreestrings.valuation.api.debtmodel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class CompanyValuationResponseDto {

    private UUID companyId;

    private List<ValuationDetailsDto> valuationDates;
}
