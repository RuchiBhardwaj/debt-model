package com.seventythreestrings.valuation.api.debtmodel.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.seventythreestrings.valuation.api.common.entity.BaseEntity;
import com.seventythreestrings.valuation.api.debtmodel.dto.DayCountConvention;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;
import org.springframework.format.annotation.DateTimeFormat;

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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @NotEmpty
    @Size(max = 255)
    @Column(name = "issuer_name")
    private String issuerName;


    @Size(max = 255)
    @Column(name = "geography")
    private String geography;

    @Size(max = 255)
    @Column(name = "sector")
    private String sector;

    @Lob
    @URL
    @Column(name = "websites")
    private String websites;

    @Size(max = 255)
    @Column(name = "security_type")
    private String securityType;


    @Lob
    @Column(name = "description")
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "exit_date")
    private LocalDate exit_date;

    @NotNull
    @NotEmpty
    @Size(max = 255)
    @Column(name = "portfolio_company_name")
    private String portfolioCompanyName;

    @NotNull
    @NotEmpty
    @Size(max = 255)
    @Column(name = "debt_security_name")
    private String debtSecurityName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "origination_date")
    private LocalDate originationDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "valuation_date")
    private LocalDate valuationDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "maturity_date")
    private LocalDate maturityDate;

    @NotNull
    @NotEmpty
    @Size(max = 255)
    private String currency;

    @NotNull
    @Column(name = "principal_amount")
    private double principalAmount;

    @NotNull
    @Column(name = "principal_outstanding")
    private double principalOutstanding;

    @NotNull
    @Column(name = "day_count_convention")
    private DayCountConvention dayCountConvention;

    @Column(name = "discount_rate")
    private double discountRate = -1;

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
