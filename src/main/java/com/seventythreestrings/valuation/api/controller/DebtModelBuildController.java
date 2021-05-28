package com.seventythreestrings.valuation.api.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/build")
public class DebtModelBuildController {
	@Value("${environment}")
	private String environment;
	@GetMapping("/environment")
	public String getEnvironment() {
		return "Debt Model Environment: "+environment;
	}
}
