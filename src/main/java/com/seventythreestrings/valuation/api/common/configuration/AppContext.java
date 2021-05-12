package com.seventythreestrings.valuation.api.common.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableAutoConfiguration
@ComponentScan
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@PropertySource(ignoreResourceNotFound = true, value = "classpath:application.properties")
public class AppContext {

    @Value("${spring.datasource.url}")
    private String dbConnectionString;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${cors.allowed-origins}")
    private String corsAllowedOrigins;

    public String[] getAllowedCorsOrigins() {
        String allowedOrigins = corsAllowedOrigins;
        if (allowedOrigins == null) {
            return new String[]{};
        }

        return allowedOrigins.split(",");
    }
}