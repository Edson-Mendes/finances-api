package br.com.emendes.financesapi.config.bean;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Classe com as configurações do OpenAPI.
 */
@Configuration
public class OpenAPIBean {

  @Bean
  public OpenAPI openAPI() {
    Contact contact = new Contact();
    contact.name("Edson Mendes").email("edson.luiz.mendes@hotmail.com").url("https://github.com/Edson-Mendes");

    SecurityScheme securityScheme = new SecurityScheme()
        .type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT");

    return new OpenAPI()
        .info(new Info().title("Finances API")
            .description("Application for financial control")
            .version("2.0.0")
            .contact(contact))
        .components(new Components()
            .addSecuritySchemes("bearer-key", securityScheme));
  }

}
