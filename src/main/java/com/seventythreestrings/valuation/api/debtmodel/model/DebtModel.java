package com.seventythreestrings.valuation.api.debtmodel.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.seventythreestrings.valuation.api.common.entity.BaseEntity;
import com.seventythreestrings.valuation.api.debtmodel.dto.DebtModelInput;
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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "portfolio_id")
    private Long portfolioId;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<DebtModelInput> inputs = new ArrayList<>();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "UTC")
    @Column(name = "analysis_date")
    private LocalDateTime analysisDate;

    @OneToOne(mappedBy="debtModel", fetch = FetchType.LAZY)
    private GeneralDetails generalDetails;

    @OneToOne(mappedBy="debtModel", fetch = FetchType.LAZY)
    private Cashflow cashflow;
}
