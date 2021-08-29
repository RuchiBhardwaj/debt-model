package com.seventythreestrings.valuation.api.debtmodel.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.seventythreestrings.valuation.api.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "look_up_debt_details")
public class LookUpDebtDetails extends BaseEntity {

    @Id
    @Type(type="org.hibernate.type.UUIDCharType")
    @Column(name = "company_id")
    private UUID companyId;

    @Column(name = "company_name")
    private String companyName;

    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long debtId;

    @Type(type="org.hibernate.type.UUIDCharType")
    @Column(name = "fund_id")
    private UUID fundId;

    @Column(name = "fund_name")
    private String fundName;

}
