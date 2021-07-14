package com.seventythreestrings.valuation.api.debtmodel.model;

import com.seventythreestrings.valuation.api.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

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

    @OneToOne
    @JoinColumn(name = "annual_historical_id", nullable = false)
    private AnnualHistoricalFinancial annualHistoricalFinancial;

    @OneToOne
    @JoinColumn(name = "annual_projected_id", nullable = false)
    private AnnualProjectedFinancial annualProjectedFinancial;

    @OneToOne
    @JoinColumn(name = "debt_model_id", nullable = false)
    private DebtModel debtModel;

}
