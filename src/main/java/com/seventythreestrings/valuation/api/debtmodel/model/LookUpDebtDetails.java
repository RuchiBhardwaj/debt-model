package com.seventythreestrings.valuation.api.debtmodel.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "lookUp_debt_details")
public class LookUpDebtDetails {
    @Id
    private Long debtId;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "company_id")
    private Long companyId;

    @Column(name = "fund_id")
    private Long fundId;

    @Column(name = "fund_name")
    private String fundName;

    @Column(name = "valuation_date_id")
    private Long valuationDateId;
}
