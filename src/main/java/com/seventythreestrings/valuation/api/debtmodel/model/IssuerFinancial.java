package com.seventythreestrings.valuation.api.debtmodel.model;

import com.seventythreestrings.valuation.api.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "issuer_financial")
public class IssuerFinancial extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;


    @Column(name = "debt_senior_issue")
    private double debtSeniorIssue;


    @Column(name = "enterprise_value")
    private double enterpriseValue;

    @OneToMany(mappedBy="issuerFinancial", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("date ASC")
    private Set<AnnualHistoricalFinancial> annualHistoricalFinancials = new HashSet<>();

    @OneToMany(mappedBy="issuerFinancial", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("date ASC")
    private Set<AnnualProjectedFinancial> annualProjectedFinancials = new HashSet<>();

    @Column(name = "version_id")
    private int versionId;

    @OneToOne
    @JoinColumn(name = "debt_model_id", nullable = false)
    private DebtModel debtModel;

}
