package com.seventythreestrings.valuation.api.debtmodel.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.seventythreestrings.valuation.api.common.entity.BaseEntity;
import com.seventythreestrings.valuation.api.debtmodel.dto.PrincipalRepaymentPattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "prepayment_details")
public class PrepaymentDetails extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "principal_repayment_pattern")
    private PrincipalRepaymentPattern principalRepaymentPattern;

    @OneToOne
    @JoinColumn(name = "debt_model_id", nullable = false)
    private DebtModel debtModel;

    @Column(name = "version_id")
    private int versionId;

    @OneToMany(mappedBy="prepaymentDetails", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("date ASC")
    private Set<PaymentSchedule> paymentSchedules = new HashSet<>();
}
