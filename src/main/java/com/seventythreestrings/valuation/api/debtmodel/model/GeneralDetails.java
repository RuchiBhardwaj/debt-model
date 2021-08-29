package com.seventythreestrings.valuation.api.debtmodel.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.seventythreestrings.valuation.api.common.converter.LocalDateAttributeConverter;
import com.seventythreestrings.valuation.api.common.entity.BaseEntity;
import com.seventythreestrings.valuation.api.debtmodel.enums.DayCountConvention;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "general_details")
public class GeneralDetails extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    @NotEmpty
    @Size(max = 255)
    @Column(name = "issuer_name")
    private String issuerName;

    @Size(max = 255)
    @Column(name = "geography")
    private String geography;

    @Lob
    @URL
    @Column(name = "website")
    private String website;

    @Size(max = 255)
    @Column(name = "sector")
    private String sector;

    @Lob
    @Column(name = "description")
    private String description;

    @Size(max = 255)
    @Column(name = "security_name")
    private String securityName;

    @Column(name = "origination_date")
    @Convert(converter = LocalDateAttributeConverter.class)
    private LocalDate originationDate;

    @Column(name = "valuation_date")
    @Convert(converter = LocalDateAttributeConverter.class)
    private LocalDate valuationDate;

    @Column(name = "exit_date")
    @Convert(converter = LocalDateAttributeConverter.class)
    private LocalDate exitDate;

    @Column(name = "maturity_date")
    @Convert(converter = LocalDateAttributeConverter.class)
    private LocalDate maturityDate;

    @NotNull
    @Column(name = "day_count_convention")
    private DayCountConvention dayCountConvention;

    @NotNull
    @NotEmpty
    @Size(max = 255)
    private String currency;

    @NotNull
    @Column(name = "principal_outstanding")
    private double principalOutstanding;

    @NotNull
    @Column(name = "principal_amount")
    private double principalAmount;

    @NotNull
    @NotEmpty
    @Size(max = 255)
    @Column(name = "portfolio_company_name")
    private String portfolioCompanyName;

    @Column(name = "percentage_of_called_down")
    private double percentageOfCalledDown;

    @Column(name = "discount_rate")
    private double discountRate = -1;

    @Column(name = "version_id")
    private int versionId;

    @OneToOne
    @JoinColumn(name = "debt_model_id", nullable = false)
    private DebtModel debtModel;

    // TODO:
    public double getDiscountRate() {
        if (discountRate == -1) {
            return 6.12;
        }

        return discountRate;
    }
}
