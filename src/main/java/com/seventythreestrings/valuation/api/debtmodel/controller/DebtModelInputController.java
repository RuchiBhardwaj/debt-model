package com.seventythreestrings.valuation.api.debtmodel.controller;

import com.seventythreestrings.valuation.api.debtmodel.dto.DebtModelInput;
import com.seventythreestrings.valuation.api.debtmodel.dto.DebtModelInputDto;
import com.seventythreestrings.valuation.api.debtmodel.service.DebtModelInputService;
import com.seventythreestrings.valuation.api.util.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/api/v1/valuation/debt-model/{debtModelId}/input")
@Slf4j
public class DebtModelInputController {

    private final DebtModelInputService debtModelInputService;

    @Autowired
    public DebtModelInputController(DebtModelInputService debtModelInputService) {
        this.debtModelInputService = debtModelInputService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DebtModelInputDto>>> getInputsForDebtModel(@PathVariable(value = "debtModelId") @NotNull Long debtModelId) {
        List<DebtModelInputDto> inputs = debtModelInputService.getInputsForDebtModel(debtModelId);

        ApiResponse<List<DebtModelInputDto>> apiResponse = new ApiResponse<>();
        apiResponse.setSuccess(true);
        apiResponse.setResponse(inputs);
        apiResponse.setMessage("Debt Model Inputs");
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PostMapping("/{inputType}")
    public ResponseEntity<ApiResponse<DebtModelInputDto>> create(
            @PathVariable(value = "debtModelId") @NotNull Long debtModelId,
            @PathVariable(value = "inputType") @NotNull DebtModelInput inputType,
            @RequestBody DebtModelInputDto debtModelInputDto) {
        Object payload = debtModelInputDto.getPayload();

        Object model = debtModelInputService.create(inputType, payload, debtModelId);
        DebtModelInputDto input = new DebtModelInputDto(inputType, model);

        ApiResponse<DebtModelInputDto> apiResponse = new ApiResponse<>();
        apiResponse.setSuccess(true);
        apiResponse.setResponse(input);
        apiResponse.setMessage("Debt Model Input Created successfully");
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/{inputType}/{id}")
    public ResponseEntity<ApiResponse<DebtModelInputDto>> get(
            @PathVariable(value = "debtModelId") @NotNull Long debtModelId,
            @PathVariable(value = "inputType") @NotNull DebtModelInput inputType,
            @PathVariable(value = "id") @NotNull Long id ) {
        Object input = debtModelInputService.get(inputType, id);
        DebtModelInputDto debtModelInputDto = new DebtModelInputDto(inputType, input);

        ApiResponse<DebtModelInputDto> apiResponse = new ApiResponse<>();
        apiResponse.setSuccess(true);
        apiResponse.setResponse(debtModelInputDto);
        apiResponse.setMessage("Debt Model Input retrieved successfully");
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PutMapping("/{inputType}/{id}")
    public ResponseEntity<ApiResponse<DebtModelInputDto>> update(
            @PathVariable(value = "debtModelId") @NotNull Long debtModelId,
            @PathVariable(value = "inputType") @NotNull DebtModelInput inputType,
            @PathVariable(value = "id") @NotNull Long id,
            @RequestBody DebtModelInputDto debtModelInputDto) {
        Object payload = debtModelInputDto.getPayload();

        Object model = debtModelInputService.update(inputType, payload, debtModelId);
        DebtModelInputDto input = new DebtModelInputDto(inputType, model);

        ApiResponse<DebtModelInputDto> apiResponse = new ApiResponse<>();
        apiResponse.setSuccess(true);
        apiResponse.setResponse(input);
        apiResponse.setMessage("Debt Model Input updated successfully");
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @DeleteMapping("/{inputType}/{id}")
    public ResponseEntity<ApiResponse<Boolean>> delete(
            @PathVariable(value = "debtModelId") @NotNull Long debtModelId,
            @PathVariable(value = "inputType") @NotNull DebtModelInput inputType,
            @PathVariable(value = "id") @NotNull Long id) {
        ApiResponse<Boolean> apiResponse = new ApiResponse<>();
        debtModelInputService.delete(inputType, id, debtModelId);
        apiResponse.setSuccess(true);
        apiResponse.setResponse(true);
        apiResponse.setMessage("Debt Model Input Deleted Successfully");
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
