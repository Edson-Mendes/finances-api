package br.com.emendes.financesapi.config.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfigurations {

  @Bean
  public OpenAPI financesApi() {
    return new OpenAPI()
        .info(new Info().title("Finances API")
            .description("Application for financial control")
            .version("0.0.1-SNAPSHOT"))
        .components(new Components()
            .addSecuritySchemes("bearer-key",
                new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));
  }
}
