package com.seventythreestrings.valuation.api.debtmodel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ValuationDetailsDto {


    private UUID valuationDateID;

    private String date;

    private double value;

    private double discountRate;

    private double ytm;

    private double revenue;

    private double ebitda;

    private double cash;
}
