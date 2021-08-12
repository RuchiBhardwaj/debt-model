package com.seventythreestrings.valuation.api.debtmodel.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DebtModelDto {
    private Long id;

    private List<DebtModelInput> inputs;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "fund_id")
    private Long fundId;

    @Column(name = "fund_name")
    private String fundName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "UTC")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime analysisDate;

    private GeneralDetailsDto generalDetails;

    private CashflowDto cashflow;
}
