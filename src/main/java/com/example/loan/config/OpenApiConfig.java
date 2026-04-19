// OpenApiConfig.java
package com.example.loan.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI loanOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Loan Approval API")
                        .description("Backend for loan application and approval process")
                        .version("1.0.0"));
    }
}