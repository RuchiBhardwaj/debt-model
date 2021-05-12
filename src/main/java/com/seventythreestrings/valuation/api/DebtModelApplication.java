package com.seventythreestrings.valuation.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.TimeZone;

@SpringBootApplication
@ComponentScan("com.seventythreestrings.valuation")
public class DebtModelApplication {

	public static void main(String[] args) throws Exception{
		SpringApplication.run(DebtModelApplication.class, args);
	}

	@PostConstruct
	public void init(){
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));   // It will set UTC timezone
		System.out.println("Spring boot application running in UTC timezone :"+new Date());   // It will print UTC timezone
	}

}
