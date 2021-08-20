package com.seventythreestrings.valuation.api.debtmodel.dto;

import com.seventythreestrings.valuation.api.debtmodel.model.GeneralDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CompanyDetailsDto {
    
    private UUID companyId;

    private String companyName;
    
    private Object companyDetails;


    public CompanyDetailsDto(UUID companyId, Object companyDetails) {
        this.companyId = companyId;
        this.companyDetails = companyDetails;
    }
}
