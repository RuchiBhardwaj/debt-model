package com.seventythreestrings.valuation.api.debtmodel.dto;


import com.seventythreestrings.valuation.api.debtmodel.model.DiscountAdjustment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DiscountRateComputationDto {

    private Long id;

    private double concludedCreditSpreadQuarter1;

    private double concludedCreditSpreadMedium;

    private double concludedCreditSpreadQuarter3;

    private double riskFreeRateQuarter1;

    private double riskFreeRateMedium;

    private double riskFreeRateQuarter3;

    private double ytmQuarter1;

    private double ytmMedium;

    private double ytmQuarter3;

    private Long debtModelId;

    private List<DiscountAdjustment> discountAdjustments;

}
