package br.com.emendes.financesapi.integration;

import br.com.emendes.financesapi.dto.response.TokenResponse;
import br.com.emendes.financesapi.dto.response.UserResponse;
import br.com.emendes.financesapi.controller.dto.error.ErrorDto;
import br.com.emendes.financesapi.controller.dto.error.FormErrorDto;
import br.com.emendes.financesapi.dto.request.ChangePasswordRequest;
import br.com.emendes.financesapi.dto.request.SignInRequest;
import br.com.emendes.financesapi.model.entity.User;
import br.com.emendes.financesapi.repository.UserRepository;
import br.com.emendes.financesapi.util.wrapper.PageableResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
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

    ResponseEntity<TokenResponse> responseAdmin = requestToSignIn(new SignInRequest(adminEmail, adminPassword));

    HEADERS_ADMIN.add("Authorization", "Bearer "+responseAdmin.getBody().getToken());

    String userEmail = "user@email.com";
    String userPassword = "123456";

    ResponseEntity<TokenResponse> responseUser = requestToSignIn(new SignInRequest(userEmail, userPassword));

    HEADERS_USER.add("Authorization", "Bearer "+responseUser.getBody().getToken());
  }



  //@Test
  @DisplayName("changePassword must returns status 204 when change password successful")
  void changePassword_ReturnsStatus204_WhenChangePasswordSuccessful(){
    ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("123456", "12345678", "12345678");

    HttpEntity<ChangePasswordRequest> requestEntity = new HttpEntity<>(changePasswordRequest, HEADERS_USER);
    ResponseEntity<Void> response = testRestTemplate
        .exchange(BASE_URI+"/password", HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<>(){});

    HttpStatus statusCode = response.getStatusCode();

    Assertions.assertThat(statusCode).isEqualTo(HttpStatus.NO_CONTENT);

    String userEmail = "user@email.com";
    String userPassword = "123456";

    HttpEntity<SignInRequest> requestBodyAdmin = new HttpEntity<>(new SignInRequest(userEmail, userPassword));

    ResponseEntity<ErrorDto> responseUser = testRestTemplate.exchange(
        "/auth/signin", HttpMethod.POST, requestBodyAdmin, new ParameterizedTypeReference<>() {
        });

    Assertions.assertThat(responseUser.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    Assertions.assertThat(responseUser.getBody()).isNotNull();
    Assertions.assertThat(responseUser.getBody().getError()).isEqualTo("Bad credentials");
  }

  //@Test
  @DisplayName("changePassword must returns status 400 and errorDto when password and confirm don't matches")
  void changePassword_ReturnsStatus400AndErrorDto_WhenPasswordAndConfirmDontMatches(){
    ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("123456", "12345678", "1234567");

    HttpEntity<ChangePasswordRequest> requestEntity = new HttpEntity<>(changePasswordRequest, HEADERS_USER);
    ResponseEntity<ErrorDto> response = testRestTemplate
        .exchange(BASE_URI+"/password", HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<>(){});

    HttpStatus statusCode = response.getStatusCode();
    ErrorDto responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualTo(HttpStatus.BAD_REQUEST);
    Assertions.assertThat(responseBody).isNotNull();
    Assertions.assertThat(responseBody.getError()).isEqualTo("BAD REQUEST");
    Assertions.assertThat(responseBody.getMessage()).isEqualTo("as senhas não correspondem!");
  }

  //@Test
  @DisplayName("changePassword must returns status 400 and List<FormErrorDto> when password is not strong enough")
  void changePassword_ReturnsStatus400AndListFormErrorDto_WhenPasswordIsNotStrongEnough(){
    ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("123456","1234", "1234");

    HttpEntity<ChangePasswordRequest> requestEntity = new HttpEntity<>(changePasswordRequest, HEADERS_USER);
    ResponseEntity<List<FormErrorDto>> response = testRestTemplate
        .exchange(BASE_URI+"/password", HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<>(){});

    HttpStatus statusCode = response.getStatusCode();
    List<FormErrorDto> responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualTo(HttpStatus.BAD_REQUEST);
    Assertions.assertThat(responseBody)
        .isNotNull().contains(new FormErrorDto("newPassword", "Senha inválida"));
  }

  private ResponseEntity<TokenResponse> requestToSignIn(SignInRequest signInRequest){
    HttpEntity<SignInRequest> requestBodyAdmin = new HttpEntity<>(signInRequest);

    return testRestTemplate.exchange(
        "/auth/signin", HttpMethod.POST, requestBodyAdmin, new ParameterizedTypeReference<>() {});
  }
}