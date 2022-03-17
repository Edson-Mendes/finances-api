package br.com.emendes.financesapi.authetication;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;

import br.com.emendes.financesapi.config.validation.error_dto.ErrorDto;
import br.com.emendes.financesapi.config.validation.error_dto.FormErrorDto;
import br.com.emendes.financesapi.controller.dto.TokenDto;
import br.com.emendes.financesapi.util.CustomMockMvc;
import br.com.emendes.financesapi.util.DtoFromMvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
@ActiveProfiles("test")
public class SigninTest {

  @Autowired
  private CustomMockMvc mock;

  @BeforeAll
  public void addRoleAndUser() throws Exception {
    mock.post("/role", Map.of("name", "ROLE_USER"), "", 201);
    mock.post("/auth/signup",
        Map.of("name", "Ipsum", "email", "ipsum@email.com", "password", "123123", "confirm", "123123"), "", 201);
  }

  @Test
  @Order(1)
  public void deveriaDevolver200ETokenDtoAoLogar() throws Exception {
    String email = "ipsum@email.com";
    String password = "123123";
    Map<String, Object> params = Map.of("email", email, "password", password);

    MvcResult result = mock.post("/auth/signin", params, "", 200);

    TokenDto tokenResponse = DtoFromMvcResult.tokenDto(result);

    Assertions.assertEquals("Bearer", tokenResponse.getType());
  }

  @Test
  @Order(2)
  public void deveriaDevolver400EErrorDtoAoLogarComSenhaInvalida() throws Exception {
    String email = "ipsum@email.com";
    String password = "123";
    Map<String, Object> params = Map.of("email", email, "password", password);

    MvcResult result = mock.post("/auth/signin", params, "", 400);

    ErrorDto errorResponse = DtoFromMvcResult.errorDto(result);

    Assertions.assertEquals("Bad credentials", errorResponse.getError());
    Assertions.assertEquals("Email ou password inválidos", errorResponse.getMessage());

  }

  @Test
  @Order(3)
  public void deveriaDevolver400EErrorDtoAoLogarComEmailErrado() throws Exception {
    String email = "ips@email.com";
    String password = "123123";
    Map<String, Object> params = Map.of("email", email, "password", password);

    MvcResult result = mock.post("/auth/signin", params, "", 400);

    ErrorDto errorResponse = DtoFromMvcResult.errorDto(result);

    Assertions.assertEquals("Bad credentials", errorResponse.getError());
    Assertions.assertEquals("Email ou password inválidos", errorResponse.getMessage());

  }

  @Test
  @Order(4)
  public void deveriaDevolver400EErrorDtoAoLogarComEmailInvalido() throws Exception {
    String email = "email.com";
    String password = "123123";
    Map<String, Object> params = Map.of("email", email, "password", password);

    MvcResult result = mock.post("/auth/signin", params, "", 400);

    FormErrorDto formErrorResponse = DtoFromMvcResult.formErrorDto(result);

    Assertions.assertEquals("email", formErrorResponse.getField());
    Assertions.assertEquals("deve ser um e-mail bem formado", formErrorResponse.getError());
    
  }

}
