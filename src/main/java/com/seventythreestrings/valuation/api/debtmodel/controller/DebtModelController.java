package com.seventythreestrings.valuation.api.debtmodel.controller;

import com.seventythreestrings.valuation.api.debtmodel.dto.DebtModel;
import com.seventythreestrings.valuation.api.exception.AppException;
import com.seventythreestrings.valuation.api.exception.ErrorCodesAndMessages;
import com.seventythreestrings.valuation.api.debtmodel.service.DebtModelService;
import com.seventythreestrings.valuation.api.util.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;

@RestController
@RequestMapping("/api/v1/valuation/debt-model")
@Slf4j
public class DebtModelController {
	@Inject
	private DebtModelService debtModelService;
	@PostMapping
	public ResponseEntity<ApiResponse<List<DebtModel>>> computeDeptModel(@RequestBody String requestBody) throws AppException {
		ApiResponse<List<DebtModel>> apiResponse=new ApiResponse<>();
		try {
			List<DebtModel> debtModels=debtModelService.computeDeptModel(requestBody);
			apiResponse.setSuccess(true);
			apiResponse.setResponse(debtModels);
			apiResponse.setMessage("Debt Model Algo Completed Successfully");
			return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
		}catch (AppException e) {
			log.error("AppException While Analysing Debt Model Algo: "+e.getAppErrorMessage(), e);
			apiResponse.setSuccess(false);
			apiResponse.setMessage(e.getAppErrorMessage());
			apiResponse.setErrorCode(e.getCode());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
		} catch (Exception e) {
			log.error("Exception While Analysing Debt Model Algo: "+e.getMessage(), e);
			apiResponse.setSuccess(false);
			apiResponse.setMessage(e.getMessage());
			apiResponse.setErrorCode(ErrorCodesAndMessages.UNKNOWN_EXCEPTION.getCode());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
		}
	}
}
