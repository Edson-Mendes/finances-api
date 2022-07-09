package br.com.emendes.financesapi.integration;

import br.com.emendes.financesapi.controller.dto.TokenDto;
import br.com.emendes.financesapi.controller.dto.UserDto;
import br.com.emendes.financesapi.controller.dto.error.ErrorDto;
import br.com.emendes.financesapi.controller.dto.error.FormErrorDto;
import br.com.emendes.financesapi.controller.form.LoginForm;
import br.com.emendes.financesapi.controller.form.SignupForm;
import br.com.emendes.financesapi.util.creator.SignupFormCreator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@DisplayName("Integration tests for /auth/**")
class AuthenticationControllerIT {

  @Autowired
  private TestRestTemplate testRestTemplate;

  private final String BASE_URI = "/auth";

  @Test
  @DisplayName("auth must returns status 200 and TokenDto when sucessful")
  void auth_ReturnsStatus200AndTokenDto_WhenSucessful() {
    String email = "user@email.com";
    String password = "123456";
    HttpEntity<LoginForm> requestBody = new HttpEntity<>(new LoginForm(email, password));

    ResponseEntity<TokenDto> response = testRestTemplate.exchange(
        BASE_URI + "/signin", HttpMethod.POST, requestBody, new ParameterizedTypeReference<>() {
        });

    HttpStatus statusCode = response.getStatusCode();
    TokenDto responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.OK);
    Assertions.assertThat(responseBody).isNotNull();
    Assertions.assertThat(responseBody.getType()).isEqualTo("Bearer");
    Assertions.assertThat(responseBody.getToken()).isNotBlank();
  }

  @Test
  @DisplayName("auth must returns 400 and ErrorDto when password is wrong")
  void auth_Returns400AndErrorDto_WhenPasswordIsWrong() {
    String email = "user@email.com";
    String password = "1234";
    HttpEntity<LoginForm> requestBody = new HttpEntity<>(new LoginForm(email, password));

    ResponseEntity<ErrorDto> response = testRestTemplate.exchange(
        BASE_URI + "/signin", HttpMethod.POST, requestBody, new ParameterizedTypeReference<>() {
        });

    HttpStatus statusCode = response.getStatusCode();
    ErrorDto responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    Assertions.assertThat(responseBody).isNotNull();
    Assertions.assertThat(responseBody.getError()).isEqualTo("Bad credentials");
    Assertions.assertThat(responseBody.getMessage()).isEqualTo("Email ou password inválidos");
  }

  @Test
  @DisplayName("auth must returns 400 and ErrorDto when email is wrong")
  void auth_Returns400AndErrorDto_WhenEmailIsWrong() {
    String email = "invalid@email.com";
    String password = "123456";
    HttpEntity<LoginForm> requestBody = new HttpEntity<>(new LoginForm(email, password));

    ResponseEntity<ErrorDto> response = testRestTemplate.exchange(
        BASE_URI + "/signin", HttpMethod.POST, requestBody, new ParameterizedTypeReference<>() {
        });

    HttpStatus statusCode = response.getStatusCode();
    ErrorDto responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    Assertions.assertThat(responseBody).isNotNull();
    Assertions.assertThat(responseBody.getError()).isEqualTo("Bad credentials");
    Assertions.assertThat(responseBody.getMessage()).isEqualTo("Email ou password inválidos");
  }

  @Test
  @DisplayName("auth must returns 400 and List<FormErrorDto> when email is invalid")
  void auth_Returns400AndListFormErrorDto_WhenEmailIsInvalid() {
    String email = "invalidemailcom";
    String password = "123456";
    HttpEntity<LoginForm> requestBody = new HttpEntity<>(new LoginForm(email, password));

    ResponseEntity<List<FormErrorDto>> response = testRestTemplate.exchange(
        BASE_URI + "/signin", HttpMethod.POST, requestBody, new ParameterizedTypeReference<>() {
        });

    HttpStatus statusCode = response.getStatusCode();
    List<FormErrorDto> responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    Assertions.assertThat(responseBody).isNotNull();
    Assertions.assertThat(responseBody.get(0).getField()).isEqualTo("email");
    Assertions.assertThat(responseBody.get(0).getError()).isEqualTo("deve ser um e-mail bem formado");
  }

  @Test
  @DisplayName("register must returns status 200 and UserDto when created sucessful")
  void register_ReturnsStatus200AndUserDto_WhenCreatedSucessful() {
    String name = "New User";
    String email = "newuser@email.com";
    HttpEntity<SignupForm> requestBody = new HttpEntity<>(SignupFormCreator.withNameAndEmail(name, email));

    ResponseEntity<UserDto> response = testRestTemplate.exchange(
        BASE_URI + "/signup", HttpMethod.POST, requestBody, new ParameterizedTypeReference<>() {
        });

    HttpStatus statusCode = response.getStatusCode();
    UserDto responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.CREATED);
    Assertions.assertThat(responseBody).isNotNull();
    Assertions.assertThat(responseBody.getName()).isEqualTo("New User");
    Assertions.assertThat(responseBody.getEmail()).isEqualTo("newuser@email.com");
  }

  @Test
  @DisplayName("register must returns status 400 and List<FormErrorDto> when form is invalid")
  void register_ReturnsStatus400AndListFormErrorDto_WhenFormIsInvalid() {
    String name = "";
    String email = "newuseremailcom";
    String password = "1234";
    String confirm = "";

    HttpEntity<SignupForm> requestBody = new HttpEntity<>(new SignupForm(name, email, password, confirm));

    ResponseEntity<List<FormErrorDto>> response = testRestTemplate.exchange(
        BASE_URI + "/signup", HttpMethod.POST, requestBody, new ParameterizedTypeReference<>() {
        });

    HttpStatus statusCode = response.getStatusCode();
    List<FormErrorDto> responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    Assertions.assertThat(responseBody)
        .isNotNull().isNotEmpty().hasSize(5)
        .contains(new FormErrorDto("name", "não deve estar em branco"))
        .contains(new FormErrorDto("email", "deve ser um endereço de e-mail bem formado"))
        .contains(new FormErrorDto("password", "Senha inválida"))
        .contains(new FormErrorDto("password", "deve ter de 8 a 30 caracteres."))
        .contains(new FormErrorDto("confirm", "não deve estar em branco"));
  }

  @Test
  @DisplayName("register must returns status 400 and ErrorDto when password and confirm don't matches")
  void register_ReturnsStatus400AndErrorDto_WhenPasswordAndConfirmDontMatches() {
    String name = "New User";
    String email = "newuser@email.com";
    String password = "1234567890";
    String confirm = "1234567800";

    HttpEntity<SignupForm> requestBody = new HttpEntity<>(new SignupForm(name, email, password, confirm));

    ResponseEntity<ErrorDto> response = testRestTemplate.exchange(
        BASE_URI + "/signup", HttpMethod.POST, requestBody, new ParameterizedTypeReference<>() {
        });

    HttpStatus statusCode = response.getStatusCode();
    ErrorDto responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    Assertions.assertThat(responseBody).isNotNull();
    Assertions.assertThat(responseBody.getError()).isEqualTo("BAD REQUEST");
    Assertions.assertThat(responseBody.getMessage()).isEqualTo("As senhas não correspondem!");
  }

  @Test
  @DisplayName("register must returns status 409 and ErrorDto when email is already in use")
  void register_ReturnsStatus409AndErrorDto_WhenPasswordAndConfirmDontMatches() {
    String name = "New User";
    String email = "user@email.com";
    String password = "1234567890";
    String confirm = "1234567890";

    HttpEntity<SignupForm> requestBody = new HttpEntity<>(new SignupForm(name, email, password, confirm));

    ResponseEntity<ErrorDto> response = testRestTemplate.exchange(
        BASE_URI + "/signup", HttpMethod.POST, requestBody, new ParameterizedTypeReference<>() {
        });

    HttpStatus statusCode = response.getStatusCode();
    ErrorDto responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.CONFLICT);
    Assertions.assertThat(responseBody).isNotNull();
    Assertions.assertThat(responseBody.getError()).isEqualTo("CONFLICT");
    Assertions.assertThat(responseBody.getMessage()).isEqualTo("Email inserido já está em uso!");
  }

}