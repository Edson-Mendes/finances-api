package br.com.emendes.financesapi.config.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

// import springfox.documentation.builders.ParameterBuilder;
// import springfox.documentation.builders.PathSelectors;
// import springfox.documentation.builders.RequestHandlerSelectors;
// import springfox.documentation.schema.ModelRef;
// import springfox.documentation.spi.DocumentationType;
// import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfigurations {

  // @Bean
  // public Docket financesApi() {
  // return new Docket(DocumentationType.SWAGGER_2)
  // .select()
  // .apis(RequestHandlerSelectors.basePackage("br.com.emendes.financesapi"))
  // .paths(PathSelectors.ant("/**"))
  // .build()
  // .ignoredParameterTypes(Sort.class)
  // .ignoredParameterTypes(Pageable.class)
  // .ignoredParameterTypes(Page.class)
  // .globalOperationParameters(Arrays.asList(
  // new ParameterBuilder()
  // .name("Authorization")
  // .description("Header para token JWT")
  // .modelRef(new ModelRef("string"))
  // .parameterType("header")
  // .required(false)
  // .build()));
  // }

  @Bean
  public OpenAPI financesApi() {
    return new OpenAPI()
        .info(new Info().title("SpringShop API")
            .description("application for financial control")
            .version("0.0.1-SNAPSHOT"))
        .components(new Components()
            .addSecuritySchemes("authorizationByBearerToken", new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name("authorization")))
        // AddSecurityItem section applies created scheme globally
        .addSecurityItem(new SecurityRequirement().addList("authorizationByBearerToken"));
  }
}
