package com.seventythreestrings.valuation.api.debtmodel.controller;

import com.seventythreestrings.valuation.api.debtmodel.dto.*;
import com.seventythreestrings.valuation.api.debtmodel.model.*;
import com.seventythreestrings.valuation.api.debtmodel.service.CashflowService;
import com.seventythreestrings.valuation.api.debtmodel.service.DebtModelInputService;
import com.seventythreestrings.valuation.api.debtmodel.service.DebtModelService;
import com.seventythreestrings.valuation.api.exception.ErrorCodesAndMessages;
import com.seventythreestrings.valuation.api.util.ApiResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/valuation/debt-model")
@Slf4j
public class DebtModelController {
	private final DebtModelService debtModelService;
	private final CashflowService cashflowService;
	private final ModelMapper modelMapper;
	private final DebtModelInputService debtModelInputService;

	@Autowired
	public DebtModelController(DebtModelService debtModelService, CashflowService cashflowService, ModelMapper modelMapper, DebtModelInputService debtModelInputService) {
		this.debtModelService = debtModelService;
		this.cashflowService = cashflowService;
		this.modelMapper = modelMapper;
		this.debtModelInputService = debtModelInputService;
	}

	@GetMapping
	public ResponseEntity<ApiResponse<PaginatedResponse<DebtModelDto>>> getAll(
			@RequestParam(value = "sortField", defaultValue = "createdAt", required = false) String sortField,
			@RequestParam(value = "sortOrder", defaultValue = "DESC", required = false) SortOrder sortOrder,
			@RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
			@RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {

		Page<DebtModel> debtModelPage = debtModelService.getAllPaginatedWithGeneralDetailsAndCashflow(sortField, sortOrder, pageNumber, pageSize);
		List<DebtModelDto> debtModelDtos = debtModelPage.getContent().stream()
				.map(debtModel -> modelMapper.map(debtModel, DebtModelDto.class))
				.collect(Collectors.toList());
		PaginatedResponse<DebtModelDto> response = new PaginatedResponse<>(debtModelDtos, pageNumber, pageSize, debtModelPage.getTotalPages(), debtModelPage.getTotalElements());

		ApiResponse<PaginatedResponse<DebtModelDto>> apiResponse = new ApiResponse<>();
		apiResponse.setSuccess(true);
		apiResponse.setResponse(response);
		apiResponse.setMessage("Debt Model list");
		return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
	}

	@PostMapping
	public ResponseEntity<ApiResponse<DebtModelDto>> create(@RequestBody DebtModelDto debtModelDto) {
		ApiResponse<DebtModelDto> apiResponse = new ApiResponse<>();
		DebtModel debtModel = modelMapper.map(debtModelDto, DebtModel.class);
		try {
			debtModel = debtModelService.create(debtModel);
			apiResponse.setSuccess(true);
			apiResponse.setResponse(modelMapper.map(debtModel, DebtModelDto.class));
			apiResponse.setMessage("Debt Model Created Successfully");
			return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
		} catch (Exception e) {
			apiResponse.setSuccess(false);
			apiResponse.setMessage(e.getMessage());
			apiResponse.setErrorCode(ErrorCodesAndMessages.UNKNOWN_EXCEPTION.getCode());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<DebtModelDto>> get(@PathVariable(value = "id") @NotNull Long id) {
		ApiResponse<DebtModelDto> apiResponse = new ApiResponse<>();
		DebtModel debtModel = debtModelService.get(id);
		apiResponse.setSuccess(true);
		apiResponse.setResponse(modelMapper.map(debtModel, DebtModelDto.class));
		return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
	}

	@GetMapping("/{portfolioId}/value")
	public ResponseEntity<ApiResponse<DebtModelDto>> getListOfDebt(@PathVariable(value = "portfolioId") @NotNull Long portfolioId) {
		ApiResponse<DebtModelDto> apiResponse = new ApiResponse<>();
		List<DebtModelDto> debtModel = debtModelService.getListOfDebtModels(portfolioId);
		apiResponse.setSuccess(true);
		apiResponse.setResponse(modelMapper.map(debtModel, DebtModelDto.class));
		return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<DebtModelDto>> update(@RequestBody DebtModelDto debtModelDto, @PathVariable(value = "id") @NotNull Long id) {
		ApiResponse<DebtModelDto> apiResponse = new ApiResponse<>();
		DebtModel debtModel = modelMapper.map(debtModelDto, DebtModel.class);
		debtModel.setId(id);

		debtModel = debtModelService.update(debtModel);
		apiResponse.setSuccess(true);
		apiResponse.setResponse(modelMapper.map(debtModel, DebtModelDto.class));
		apiResponse.setMessage("Debt Model Updated Successfully");
		return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Boolean>> delete(@PathVariable(value = "id") @NotNull Long id) {
		ApiResponse<Boolean> apiResponse = new ApiResponse<>();
		debtModelService.delete(id);
		apiResponse.setSuccess(true);
		apiResponse.setResponse(true);
		apiResponse.setMessage("Debt Model Deleted Successfully");
		return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
	}

	@PostMapping("/{id}/cashflow")
	public ResponseEntity<ApiResponse<CashflowDto>> generateCashflowForDebtModel(@PathVariable(value = "id") @NotNull Long id) {
		ApiResponse<CashflowDto> apiResponse = new ApiResponse<>();
		Cashflow cashflow = cashflowService.generateCashflowForDebtModel(id);
		apiResponse.setSuccess(true);
		apiResponse.setResponse(modelMapper.map(cashflow, CashflowDto.class));
		apiResponse.setMessage("Cashflow Analysis completed for Debt Instrument");
		return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
	}

	@GetMapping("/{id}/cashflow")
	public ResponseEntity<ApiResponse<CashflowDto>> getCashflowForDebtModel(@PathVariable(value = "id") @NotNull Long id) {
		ApiResponse<CashflowDto> apiResponse = new ApiResponse<>();
		Cashflow cashflow = cashflowService.getCashflowForDebtModel(id);
		apiResponse.setSuccess(true);
		apiResponse.setResponse(modelMapper.map(cashflow, CashflowDto.class));
		apiResponse.setMessage("Cashflow for Debt Instrument");
		return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
	}

	@DeleteMapping("/{id}/cashflow")
	public ResponseEntity<ApiResponse<Boolean>> deleteCashflowForDebtModel(@PathVariable(value = "id") @NotNull Long id) {
		ApiResponse<Boolean> apiResponse = new ApiResponse<>();
		cashflowService.deleteCashflowForDebtModel(id);
		apiResponse.setSuccess(true);
		apiResponse.setResponse(true);
		apiResponse.setMessage("Deleted Cashflow for Debt Instrument");
		return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
	}

	// TODO: Temp APIs, to be removed
	@SneakyThrows
	@PostMapping("/{id}/clone")
	public ResponseEntity<ApiResponse<DebtModelDto>> clone(@PathVariable(value = "id") @NotNull Long id) {
		DebtModel existingDebtModel = debtModelService.get(id);

		// create new
		DebtModel debtModel = new DebtModel();
		List<DebtModelInput> debtModelInputs = new ArrayList<>(existingDebtModel.getInputs());
		debtModel.setInputs(debtModelInputs);
		debtModel.setGeneralDetails(null);
		debtModel.setCashflow(null);
		debtModel.setAnalysisDate(null);
		debtModelService.save(debtModel);

		List<DebtModelInputDto> inputs = debtModelInputService.getInputsForDebtModel(id);
		for (DebtModelInputDto input: inputs) {
			Object payload = input.getPayload();
			switch (input.getInputType()) {
				case GENERAL_DETAILS:
					GeneralDetails generalDetails = modelMapper.map(payload, GeneralDetails.class);
					generalDetails.setId(null);
					generalDetails.setDebtModel(debtModel);
					payload = modelMapper.map(generalDetails, GeneralDetailsDto.class);
					break;
				case INTEREST_DETAILS:
					InterestDetails interestDetails = modelMapper.map(payload, InterestDetails.class);
					interestDetails.setId(null);
					interestDetails.setDebtModel(debtModel);
					payload = modelMapper.map(interestDetails, InterestDetailsDto.class);
					break;
				case PREPAYMENT_DETAILS:
					PrepaymentDetails prepaymentDetails = modelMapper.map(payload, PrepaymentDetails.class);
					prepaymentDetails.setId(null);
					prepaymentDetails.getPaymentSchedules().forEach(paymentSchedule -> paymentSchedule.setId(null));
					prepaymentDetails.setDebtModel(debtModel);
					payload = modelMapper.map(prepaymentDetails, PrepaymentDetailsDto.class);
					break;
				default:
					break;
			}
			debtModelInputService.create(input.getInputType(), payload, debtModel.getId());
		}
		return get(debtModel.getId());
	}
}
