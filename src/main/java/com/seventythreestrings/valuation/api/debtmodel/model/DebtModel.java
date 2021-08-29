package com.seventythreestrings.valuation.api.debtmodel.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.seventythreestrings.valuation.api.common.converter.LocalDateTimeAttributeConverter;
import com.seventythreestrings.valuation.api.common.entity.BaseEntity;
import com.seventythreestrings.valuation.api.debtmodel.enums.DebtModelInput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "debt_model")
@NamedEntityGraphs({
        @NamedEntityGraph(
                name = DebtModel.WITH_INPUTS,
                attributeNodes = {
                        @NamedAttributeNode("inputs")
                }
        ),
        @NamedEntityGraph(
                name = DebtModel.WITH_GENERAL_DETAILS_AND_CASHFLOW,
                attributeNodes = {
                        @NamedAttributeNode("generalDetails"),
                        @NamedAttributeNode("cashflow")
                }
        )
})
public class DebtModel extends BaseEntity {

    public static final String WITH_INPUTS = "debt-model-with-inputs";

    public static final String WITH_GENERAL_DETAILS_AND_CASHFLOW = "debt-model-with-general-details-and-cashflow";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "fund_id")
    private Long fundId;

    @Column(name = "fund_name")
    private String fundName;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private List<DebtModelInput> inputs = new ArrayList<>();

    @Column(name = "analysis_date")
    @Convert(converter = LocalDateTimeAttributeConverter.class)
    private LocalDateTime analysisDate;

    @OneToOne(mappedBy="debtModel", fetch = FetchType.LAZY)
    private GeneralDetails generalDetails;

    @OneToOne(mappedBy="debtModel", fetch = FetchType.LAZY)
    private Cashflow cashflow;
}
