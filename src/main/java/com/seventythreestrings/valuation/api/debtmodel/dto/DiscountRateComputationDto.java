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

    private double concludedCreditSpreadQuartile1;

    private double concludedCreditSpreadMedian;

    private double concludedCreditSpreadQuartile3;

    private double riskFreeRateQuartile1;

    private double riskFreeRateMedian;

    private double riskFreeRateQuartile3;

    private double ytmQuartile1;

    private double ytmMedian;

    private double ytmQuartile3;

    private Long debtModelId;

    private List<DiscountAdjustment> discountAdjustments;
}
