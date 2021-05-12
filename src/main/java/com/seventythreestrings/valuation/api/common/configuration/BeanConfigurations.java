package com.seventythreestrings.valuation.api.common.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "aware")
public class BeanConfigurations<T> implements WebMvcConfigurer {

    AppContext appContext;

    @Autowired
    BeanConfigurations(AppContext appContext) {
        this.appContext = appContext;
    }

    @Bean
    public AuditorAware<String> aware() {
        return () -> Optional.of("system");
    }

    @Bean
    public ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper=new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JSR310Module());
        return objectMapper;
    }
}
