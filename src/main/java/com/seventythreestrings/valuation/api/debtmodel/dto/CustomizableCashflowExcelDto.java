package com.seventythreestrings.valuation.api.debtmodel.dto;

import lombok.Data;

import java.util.List;

@Data
public class CustomizableCashflowExcelDto extends BaseCustomizableCashflowDto {
    private List<InterimPaymentDetailsDto> interimPaymentDetails;
}
