package br.com.emendes.financesapi.authetication;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
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
import br.com.emendes.financesapi.controller.dto.UserDto;
import br.com.emendes.financesapi.util.CustomMockMvc;
import br.com.emendes.financesapi.util.DtoFromMvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
@ActiveProfiles("test")
public class SignupTest {

  @Autowired
  private CustomMockMvc mock;

  @BeforeAll
  public void addRoles() throws Exception {
    mock.post("/role", Map.of("name", "ROLE_USER"), "", 201);
  }

  @Test
  @Order(1)
  public void deveriaDevolver201AoRealizarSignup() throws Exception {
    String name = "Ipsum Lorem";
    String email = "ipsum.l@email.com";
    String password = "12345678";
    String confirm = "12345678";

    MvcResult result = mock.post("/auth/signup",
        Map.of("name", name, "email", email, "password", password, "confirm", confirm), "", 201);
    UserDto userDtoResponse = DtoFromMvcResult.userDto(result);

    UserDto userDtoExpected = new UserDto(1l, name, email);
    Assertions.assertEquals(userDtoExpected.getName(), userDtoResponse.getName());
    Assertions.assertEquals(userDtoExpected.getEmail(), userDtoResponse.getEmail());
  }

  @Test
  @Order(2)
  public void deveriaDevolver409AoCadastrarComEmailExistenteNoDB() throws Exception {
    String name = "Ipsom";
    String email = "ipsum.l@email.com";
    String password = "111111";
    String confirm = "111111";

    mock.post("/auth/signup", Map.of("name", name, "email", email, "password", password, "confirm", confirm), "", 409);
  }

  @Test
  @Order(3)
  public void deveriaDevolver400AoCadastrarComNomeVazioEARespostaBater() throws Exception {
    String name = "";
    String email = "lorem@email.com";
    String password = "111111";
    String confirm = "111111";

    MvcResult result = mock.post("/auth/signup",
        Map.of("name", name, "email", email, "password", password, "confirm", confirm), "", 400);
    FormErrorDto formErrorDto = DtoFromMvcResult.formErrorDto(result);

    Assertions.assertEquals("name", formErrorDto.getField());
    Assertions.assertEquals("não deve estar em branco", formErrorDto.getError());

  }

  @Test
  @Order(4)
  public void deveriaDevolver400AoCadastrarComEmailInvalidoEARespostaBater() throws Exception {
    String name = "lorem";
    String email = "lorememail.com";
    String password = "111111";
    String confirm = "111111";

    MvcResult result = mock.post("/auth/signup",
        Map.of("name", name, "email", email, "password", password, "confirm", confirm), "", 400);
    // result.getResponse().setCharacterEncoding("UTF-8");
    FormErrorDto formErrorDto = DtoFromMvcResult.formErrorDto(result);

    Assertions.assertEquals("email", formErrorDto.getField());
    Assertions.assertEquals("deve ser um endereço de e-mail bem formado", formErrorDto.getError());
    
  }

  @Test
  @Order(5)
  public void deveriaDevolver400AoCadastrarComSenhaVazioEARespostaBater() throws Exception {
    String name = "lorem";
    String email = "lorem@email.com";
    String password = "";
    String confirm = "111111";

    MvcResult result = mock.post("/auth/signup",
        Map.of("name", name, "email", email, "password", password, "confirm", confirm), "", 400);
    FormErrorDto formErrorDto = DtoFromMvcResult.formErrorDto(result);

    Assertions.assertEquals("password", formErrorDto.getField());
    Assertions.assertEquals("não deve estar em branco", formErrorDto.getError());
  }

  @Test
  @Order(6)
  public void deveriaDevolver400AoCadastrarComConfimacaoVazioEARespostaBater() throws Exception {
    String name = "lorem";
    String email = "lorem@email.com";
    String password = "111111";
    String confirm = "";

    MvcResult result = mock.post("/auth/signup",
        Map.of("name", name, "email", email, "password", password, "confirm", confirm), "", 400);
    FormErrorDto formErrorDto = DtoFromMvcResult.formErrorDto(result);

    Assertions.assertEquals("confirm", formErrorDto.getField());
    Assertions.assertEquals("não deve estar em branco", formErrorDto.getError());
  }

  @Test
  @Order(7)
  public void deveriaDevolver400AoCadastrarSemAsSenhasCondizerem() throws Exception {
    String name = "lorem";
    String email = "lorem@email.com";
    String password = "111111";
    String confirm = "11111";

    MvcResult result = mock.post("/auth/signup",
        Map.of("name", name, "email", email, "password", password, "confirm", confirm), "", 400);
    ErrorDto errorDto = DtoFromMvcResult.errorDto(result);

    Assertions.assertEquals("Bad Request", errorDto.getError());
    Assertions.assertEquals("As senhas não correspondem!", errorDto.getMessage());
  }

}
