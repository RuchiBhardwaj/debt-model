package com.seventythreestrings.valuation.api.debtmodel.model;

import com.seventythreestrings.valuation.api.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "issuer_financial")
public class IssuerFinancial extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    @Column(name = "debt_senior_issue")
    private double debtSeniorIssue;


    @Column(name = "enterprise_value")
    private double enterpriseValue;

    @OneToOne
    @JoinColumn(name = "annual_historical_id", nullable = false)
    private AnnualHistoricalFinancial annualHistoricalFinancial;

    @OneToOne
    @JoinColumn(name = "annual_projected_id", nullable = false)
    private AnnualProjectedFinancial annualProjectedFinancial;

    @Column(name = "version_id")
    private int versionId;

    @OneToOne
    @JoinColumn(name = "debt_model_id", nullable = false)
    private DebtModel debtModel;

}
