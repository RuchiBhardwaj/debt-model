package com.seventythreestrings.valuation.api.debtmodel.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.seventythreestrings.valuation.api.common.entity.BaseEntity;
import com.seventythreestrings.valuation.api.debtmodel.enums.CashflowDates;
import com.seventythreestrings.valuation.api.debtmodel.enums.CustomizableCashflowType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "customizable_cashflow_excel")
public class CustomizableCashflowExcel extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "name_of_the_property")
    private String nameOfTheProperty;

    @Column(name = "cashflow_type")
    @Enumerated(EnumType.STRING)
    private CustomizableCashflowType cashflowType;

    @Column(name = "cashflow_dates")
    @Enumerated(EnumType.STRING)
    private CashflowDates cashflowDates;

    @OneToOne
    @JoinColumn(name = "debt_model_id", nullable = false)
    private DebtModel debtModel;

    @Column(name = "version_id")
    private int versionId;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @OneToMany(mappedBy="customizableCashflowExcel", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("date ASC")
    private Set<InterimPaymentDetails> interimPaymentDetails = new HashSet<>();
}
