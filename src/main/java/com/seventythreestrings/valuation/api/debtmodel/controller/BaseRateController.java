package com.seventythreestrings.valuation.api.debtmodel.controller;

import com.seventythreestrings.valuation.api.debtmodel.dto.BaseRateCurveDto;
import com.seventythreestrings.valuation.api.debtmodel.dto.BaseRateDto;
import com.seventythreestrings.valuation.api.debtmodel.dto.CashflowDto;
import com.seventythreestrings.valuation.api.debtmodel.model.BaseRate;
import com.seventythreestrings.valuation.api.debtmodel.model.BaseRateCurve;
import com.seventythreestrings.valuation.api.debtmodel.model.Cashflow;
import com.seventythreestrings.valuation.api.debtmodel.service.BaseRateCurveService;
import com.seventythreestrings.valuation.api.debtmodel.service.BaseRateService;
import com.seventythreestrings.valuation.api.util.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1/valuation/base-rate")
public class BaseRateController {
    private final BaseRateService baseRateService;
    private final BaseRateCurveService baseRateCurveService;
    private final ModelMapper modelMapper;

    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<BaseRateDto>>> getAll() {
        ApiResponse<List<BaseRateDto>> apiResponse = new ApiResponse<>();
        List<BaseRate> baseRates = baseRateService.getAll();
        List<BaseRateDto> baseRateDtos = baseRates.stream()
                .map(baseRate -> modelMapper.map(baseRate, BaseRateDto.class))
                .collect(Collectors.toList());

        apiResponse.setSuccess(true);
        apiResponse.setResponse(baseRateDtos);
        apiResponse.setMessage("Base Rates");
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/{id}/curves")
    public ResponseEntity<ApiResponse<List<BaseRateCurveDto>>> getCurvesForBaseRate(@PathVariable(value = "id") @NotNull Long id) {
        ApiResponse<List<BaseRateCurveDto>> apiResponse = new ApiResponse<>();
        List<BaseRateCurve> baseRateCurves = baseRateCurveService.getCurvesForBaseRate(id);
        List<BaseRateCurveDto> baseRateCurveDtos = baseRateCurves.stream()
                .map(baseRateCurve -> modelMapper.map(baseRateCurve, BaseRateCurveDto.class))
                .collect(Collectors.toList());

        apiResponse.setSuccess(true);
        apiResponse.setResponse(baseRateCurveDtos);
        apiResponse.setMessage("Curves for Base Rate");
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
