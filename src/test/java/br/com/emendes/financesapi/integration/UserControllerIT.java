package br.com.emendes.financesapi.integration;

import br.com.emendes.financesapi.controller.dto.TokenDto;
import br.com.emendes.financesapi.controller.dto.UserDto;
import br.com.emendes.financesapi.controller.dto.error.ErrorDto;
import br.com.emendes.financesapi.controller.dto.error.FormErrorDto;
import br.com.emendes.financesapi.controller.form.ChangePasswordForm;
import br.com.emendes.financesapi.controller.form.LoginForm;
import br.com.emendes.financesapi.model.User;
import br.com.emendes.financesapi.repository.UserRepository;
import br.com.emendes.financesapi.util.wrapper.PageableResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@DisplayName("Integration tests for /users/**")
class UserControllerIT {

  @Autowired
  private TestRestTemplate testRestTemplate;
  @Autowired
  private UserRepository userRepository;

  private final String BASE_URI = "/users";
  private final HttpHeaders HEADERS_ADMIN = new HttpHeaders();
  private final HttpHeaders HEADERS_USER = new HttpHeaders();

  @BeforeAll
  public void singInAndAddAuthorizationHeader(){
    String adminEmail = "admin@email.com";
    String adminPassword = "123456";

    ResponseEntity<TokenDto> responseAdmin = requestToSignIn(new LoginForm(adminEmail, adminPassword));

    HEADERS_ADMIN.add("Authorization", "Bearer "+responseAdmin.getBody().getToken());

    String userEmail = "user@email.com";
    String userPassword = "123456";

    ResponseEntity<TokenDto> responseUser = requestToSignIn(new LoginForm(userEmail, userPassword));

    HEADERS_USER.add("Authorization", "Bearer "+responseUser.getBody().getToken());
  }

  @Test
  @DisplayName("readAll must returns Page<UserDto> when role is admin")
  void readAll_ReturnsPageUserDto_WhenRoleIsAdmin(){
    HttpEntity<Void> requestEntity = new HttpEntity<>(HEADERS_ADMIN);
    ResponseEntity<PageableResponse<UserDto>> response = testRestTemplate
        .exchange(BASE_URI, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>(){});

    HttpStatus statusCode = response.getStatusCode();
    Page<UserDto> responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(responseBody).isNotNull().isNotEmpty().hasSize(2);
    Assertions.assertThat(responseBody.getContent().get(0).getName()).isEqualTo("User Admin");
    Assertions.assertThat(responseBody.getContent().get(0).getEmail()).isEqualTo("admin@email.com");
    Assertions.assertThat(responseBody.getContent().get(1).getName()).isEqualTo("User Common");
    Assertions.assertThat(responseBody.getContent().get(1).getEmail()).isEqualTo("user@email.com");
  }

  @Test
  @DisplayName("readAll must returns status 403 when role is user")
  void readAll_ReturnsStatus403_WhenRoleIsUser(){
    HttpEntity<Void> requestEntity = new HttpEntity<>(HEADERS_USER);
    ResponseEntity<Void> response = testRestTemplate
        .exchange(BASE_URI, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>(){});

    HttpStatus statusCode = response.getStatusCode();

    Assertions.assertThat(statusCode).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  @DisplayName("delete must returns status 204 when deleted successful")
  void delete_ReturnsStatus204_WhenDeletedSuccessful(){
    HttpEntity<Void> requestEntity = new HttpEntity<>(HEADERS_ADMIN);
    ResponseEntity<Void> response = testRestTemplate
        .exchange(BASE_URI+"/2", HttpMethod.DELETE, requestEntity, new ParameterizedTypeReference<>(){});

    HttpStatus statusCode = response.getStatusCode();

    Optional<User> optionalUser = userRepository.findById(2L);

    Assertions.assertThat(statusCode).isEqualTo(HttpStatus.NO_CONTENT);
    Assertions.assertThat(optionalUser).isEmpty();
  }

  @Test
  @DisplayName("delete must returns status 403 when role is user")
  void delete_ReturnsStatus403_WhenRoleIsUser(){
    HttpEntity<Void> requestEntity = new HttpEntity<>(HEADERS_USER);
    ResponseEntity<Void> response = testRestTemplate
        .exchange(BASE_URI+"/2", HttpMethod.DELETE, requestEntity, new ParameterizedTypeReference<>(){});

    HttpStatus statusCode = response.getStatusCode();

    Assertions.assertThat(statusCode).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  @DisplayName("delete must returns status 404 and ErrorDto when id not exists")
  void delete_ReturnsStatus404AndErrorDto_WhenIdNotExists(){
    HttpEntity<Void> requestEntity = new HttpEntity<>(HEADERS_ADMIN);
    ResponseEntity<ErrorDto> response = testRestTemplate
        .exchange(BASE_URI+"/10000", HttpMethod.DELETE, requestEntity, new ParameterizedTypeReference<>(){});

    HttpStatus statusCode = response.getStatusCode();
    ErrorDto responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualTo(HttpStatus.NOT_FOUND);
    Assertions.assertThat(responseBody).isNotNull();
    Assertions.assertThat(responseBody.getError()).isEqualTo("Not Found");
    Assertions.assertThat(responseBody.getMessage()).contains("não existe usuário com id: ");
  }

  @Test
  @DisplayName("changePassword must returns status 204 when change password successful")
  void changePassword_ReturnsStatus204_WhenChangePasswordSuccessful(){
    ChangePasswordForm changePasswordForm = new ChangePasswordForm("123456", "12345678", "12345678");

    HttpEntity<ChangePasswordForm> requestEntity = new HttpEntity<>(changePasswordForm, HEADERS_USER);
    ResponseEntity<Void> response = testRestTemplate
        .exchange(BASE_URI+"/password", HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<>(){});

    HttpStatus statusCode = response.getStatusCode();

    Assertions.assertThat(statusCode).isEqualTo(HttpStatus.NO_CONTENT);

    String userEmail = "user@email.com";
    String userPassword = "123456";

    HttpEntity<LoginForm> requestBodyAdmin = new HttpEntity<>(new LoginForm(userEmail, userPassword));

    ResponseEntity<ErrorDto> responseUser = testRestTemplate.exchange(
        "/auth/signin", HttpMethod.POST, requestBodyAdmin, new ParameterizedTypeReference<>() {
        });

    Assertions.assertThat(responseUser.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    Assertions.assertThat(responseUser.getBody()).isNotNull();
    Assertions.assertThat(responseUser.getBody().getError()).isEqualTo("Bad credentials");
  }

  @Test
  @DisplayName("changePassword must returns status 400 and errorDto when password and confirm don't matches")
  void changePassword_ReturnsStatus400AndErrorDto_WhenPasswordAndConfirmDontMatches(){
    ChangePasswordForm changePasswordForm = new ChangePasswordForm("123456", "12345678", "1234567");

    HttpEntity<ChangePasswordForm> requestEntity = new HttpEntity<>(changePasswordForm, HEADERS_USER);
    ResponseEntity<ErrorDto> response = testRestTemplate
        .exchange(BASE_URI+"/password", HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<>(){});

    HttpStatus statusCode = response.getStatusCode();
    ErrorDto responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualTo(HttpStatus.BAD_REQUEST);
    Assertions.assertThat(responseBody).isNotNull();
    Assertions.assertThat(responseBody.getError()).isEqualTo("BAD REQUEST");
    Assertions.assertThat(responseBody.getMessage()).isEqualTo("as senhas não correspondem!");
  }

  @Test
  @DisplayName("changePassword must returns status 400 and List<FormErrorDto> when password is not strong enough")
  void changePassword_ReturnsStatus400AndListFormErrorDto_WhenPasswordIsNotStrongEnough(){
    ChangePasswordForm changePasswordForm = new ChangePasswordForm("123456","1234", "1234");

    HttpEntity<ChangePasswordForm> requestEntity = new HttpEntity<>(changePasswordForm, HEADERS_USER);
    ResponseEntity<List<FormErrorDto>> response = testRestTemplate
        .exchange(BASE_URI+"/password", HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<>(){});

    HttpStatus statusCode = response.getStatusCode();
    List<FormErrorDto> responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualTo(HttpStatus.BAD_REQUEST);
    Assertions.assertThat(responseBody)
        .isNotNull().contains(new FormErrorDto("newPassword", "Senha inválida"));
  }

  private ResponseEntity<TokenDto> requestToSignIn(LoginForm loginForm){
    HttpEntity<LoginForm> requestBodyAdmin = new HttpEntity<>(loginForm);

    return testRestTemplate.exchange(
        "/auth/signin", HttpMethod.POST, requestBodyAdmin, new ParameterizedTypeReference<>() {});
  }
}