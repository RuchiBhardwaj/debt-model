package com.seventythreestrings.valuation.api.debtmodel.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.seventythreestrings.valuation.api.common.converter.LocalDateAttributeConverter;
import com.seventythreestrings.valuation.api.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "look_up_valuation_details")
public class LookUpValuationDetails extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long Id;

    @Type(type="org.hibernate.type.UUIDCharType")
    @Column(name = "valuation_date_id")
    private UUID valuationDateId;

    @Column(name = "valuation_date")
    @Convert(converter = LocalDateAttributeConverter.class)
    private LocalDate valuationDate;

    @Column(name = "version")
    private int versionId;

    @OneToOne
    @JoinColumn(name = "company_id", nullable = false)
    private LookUpDebtDetails lookUpDebtDetails;
}
