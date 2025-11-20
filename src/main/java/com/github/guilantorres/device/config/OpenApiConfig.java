package com.github.guilantorres.device.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Device Management API")
            .version("1.0.0")
            .description("API for creating, retrieving, updating, and deleting device resources.")
            .contact(new Contact()
                .name("Guilherme Torres")
                .url("https://github.com/guilantorres") // Seu GitHub
            )
        );
  }
}
