package com.seventythreestrings.valuation.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

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
    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
    @Bean
    public ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper=new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JSR310Module());
        return objectMapper;
    }
}
