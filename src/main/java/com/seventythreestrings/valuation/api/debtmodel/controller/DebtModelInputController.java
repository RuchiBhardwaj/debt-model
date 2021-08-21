package com.seventythreestrings.valuation.api.debtmodel.controller;

import com.seventythreestrings.valuation.api.debtmodel.dto.*;
import com.seventythreestrings.valuation.api.debtmodel.service.DebtModelInputService;
import com.seventythreestrings.valuation.api.exception.ErrorCodesAndMessages;
import com.seventythreestrings.valuation.api.util.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/api/v1/valuation/debt-model/{debtModelId}/input")
@Slf4j
public class DebtModelInputController {

    private final DebtModelInputService debtModelInputService;

    private final ModelMapper modelMapper;

    @Autowired
    public DebtModelInputController(DebtModelInputService debtModelInputService,ModelMapper modelMapper) {
        this.debtModelInputService = debtModelInputService;
        this.modelMapper = modelMapper;
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

    @GetMapping("/{customizableDatesType}")
    public ResponseEntity<ApiResponse<List<CustomizableDto>>> getCustomizationCashflow(@PathVariable(value = "debtModelId") @NotNull Long debtModelId
            , @PathVariable(value = "customizableDatesType") @NotNull CashflowDates cashflowDates) {
        List<CustomizableDto> inputs = debtModelInputService.getCustomizationCashflowData(debtModelId,cashflowDates);

        ApiResponse<List<CustomizableDto>> apiResponse = new ApiResponse<>();
        apiResponse.setSuccess(true);
        apiResponse.setResponse(inputs);
        apiResponse.setMessage("Customization Cashflow");
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

    @PostMapping("Customizable/{cashflowDateType}/")
    public ResponseEntity<ApiResponse<CustomizableDto>> createCustomizableCashflow(
            @PathVariable(value = "debtModelId") @NotNull Long debtModelId,
            @PathVariable(value = "cashflowDateType") @NotNull CashflowDates cashflowDateType,
            @RequestBody CustomizableDto customizableDto){
        Object payload = customizableDto.getPayload();
        Object model = debtModelInputService.createCustomizableCashflow(cashflowDateType,payload,debtModelId);
        CustomizableDto input = new CustomizableDto(cashflowDateType,model);

        ApiResponse<CustomizableDto> apiResponse = new ApiResponse<>();
        apiResponse.setSuccess(true);
        apiResponse.setResponse(input);
        apiResponse.setMessage("Customizable cashflow is created");
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);

    }

    @PostMapping("/discount")
    public ResponseEntity<ApiResponse<DiscountRateComputationDto>> createDiscount(@RequestBody DiscountRateComputationDto discountRateComputationDto,
                                                                                  @PathVariable(value = "debtModelId",required = false)  Long debtModelId){
        ApiResponse<DiscountRateComputationDto> apiResponse = new ApiResponse<>();
        try {
            DiscountRateComputationDto discount = debtModelInputService.createDiscount(discountRateComputationDto);
            apiResponse.setSuccess(true);
            apiResponse.setResponse((modelMapper.map(discount,DiscountRateComputationDto.class)));
            apiResponse.setMessage("Debt Model Created Successfully");
            return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage(e.getMessage());
            apiResponse.setErrorCode(ErrorCodesAndMessages.UNKNOWN_EXCEPTION.getCode());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
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

    @PutMapping("dataaa/{cashflowDatesTypeId}")
    public ResponseEntity<ApiResponse<CustomizableDto>> updateCustomizableCashflow(
            @PathVariable(value = "debtModelId") @NotNull Long debtModelId,
            @PathVariable(value = "cashflowDatesTypeId") @NotNull CashflowDates cashflowDates,
            @RequestBody CustomizableDto customizableDto) {
        Object payload = customizableDto.getPayload();
        Object model = debtModelInputService.updateCustomizableCashflow(cashflowDates, payload, debtModelId);
        CustomizableDto input = new CustomizableDto(cashflowDates, model);

        ApiResponse<CustomizableDto> apiResponse = new ApiResponse<>();
        apiResponse.setSuccess(true);
        apiResponse.setResponse(input);
        apiResponse.setMessage("Customizable Cashflow Input updated successfully");
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
