package com.seventythreestrings.valuation.api.debtmodel.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.seventythreestrings.valuation.api.common.converter.LocalDateAttributeConverter;
import com.seventythreestrings.valuation.api.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "interim_payment_details")
public class InterimPaymentDetails extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    @Convert(converter = LocalDateAttributeConverter.class)
    private LocalDate date;

    @NotNull
    private double amount;

    @ManyToOne
    @JoinColumn(name = "cutomizable_cashflow_excel", nullable = false)
    private CustomizableCashflowExcel customizableCashflowExcel;
}
