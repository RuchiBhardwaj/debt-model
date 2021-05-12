package com.seventythreestrings.valuation.api.debtmodel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DebtModelInputDto {
    private DebtModelInput inputType;

    private Object payload;
}
