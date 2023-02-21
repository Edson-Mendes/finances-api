package br.com.emendes.financesapi.integration.authentication;

import br.com.emendes.financesapi.controller.dto.error.ErrorDto;
import br.com.emendes.financesapi.controller.dto.error.FormErrorDto;
import br.com.emendes.financesapi.dto.request.SignupRequest;
import br.com.emendes.financesapi.dto.response.UserResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("integration")
@DisplayName("Integration tests for POST /api/auth/signup")
class SignUpIT {

  @Autowired
  private TestRestTemplate testRestTemplate;

  private final String URI = "/api/auth/signup";

  @Test
  @DisplayName("sign up must returns status 201 and UserResponse when sign up successfully")
  void signUp_MustReturnsStatus201AndUserResponse_WhenSignUpSuccessfully() {
    SignupRequest requestBody = SignupRequest.builder()
        .name("Lorem Ipsum")
        .email("lorem@email.com")
        .password("12345678")
        .confirm("12345678")
        .build();

    HttpEntity<SignupRequest> bodyAndHeaders = new HttpEntity<>(requestBody);

    ResponseEntity<UserResponse> actualResponse = testRestTemplate.exchange(
        URI, HttpMethod.POST, bodyAndHeaders, new ParameterizedTypeReference<>() {});

    HttpStatus actualStatusCode = actualResponse.getStatusCode();
    UserResponse actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(actualStatusCode).isEqualByComparingTo(HttpStatus.CREATED);
    Assertions.assertThat(actualResponseBody).isNotNull();
    Assertions.assertThat(actualResponseBody.getId()).isNotNull().isPositive();
    Assertions.assertThat(actualResponseBody.getName()).isEqualTo("Lorem Ipsum");
    Assertions.assertThat(actualResponseBody.getEmail()).isEqualTo("lorem@email.com");
  }

  @Test
  @DisplayName("sign up must returns status 400 when request body is invalid")
  void signUp_MustReturnsStatus400_WhenRequestBodyIsInvalid() {
    SignupRequest requestBody = SignupRequest.builder()
        .name(null)
        .email("invalidemailcom")
        .password("   ")
        .confirm(null)
        .build();

    HttpEntity<SignupRequest> bodyAndHeaders = new HttpEntity<>(requestBody);

    ResponseEntity<List<FormErrorDto>> actualResponse = testRestTemplate.exchange(
        URI, HttpMethod.POST, bodyAndHeaders, new ParameterizedTypeReference<>() {});

    HttpStatus actualStatusCode = actualResponse.getStatusCode();
    List<FormErrorDto> actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(actualStatusCode).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    Assertions.assertThat(actualResponseBody).isNotNull();
    // TODO: Após modificar o body em caso de status 4xx e 5xx, adicionar novas assertivas.
  }

  @Test
  @DisplayName("sign up must returns status 400 when password and confirm do not match")
  void signUp_MustReturnsStatus400_WhenPasswordAndConfirmDoNotMatch() {
    SignupRequest requestBody = SignupRequest.builder()
        .name("Lorem Ipsum")
        .email("lorem@email.com")
        .password("12345678")
        .confirm("1234567890")
        .build();

    HttpEntity<SignupRequest> bodyAndHeaders = new HttpEntity<>(requestBody);

    ResponseEntity<UserResponse> actualResponse = testRestTemplate.exchange(
        URI, HttpMethod.POST, bodyAndHeaders, new ParameterizedTypeReference<>() {});

    HttpStatus actualStatusCode = actualResponse.getStatusCode();
    UserResponse actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(actualStatusCode).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    Assertions.assertThat(actualResponseBody).isNotNull();
    // TODO: Adicionar novas assertivas.
  }

  @Test
  @DisplayName("sign up must returns status 409 when informed email is already in use")
  @Sql(scripts = {"/sql/user/insert-user.sql"})
  void signUp_MustReturnsStatus409_WhenInformedEmailIsAlreadyInUse() {
    SignupRequest requestBody = SignupRequest.builder()
        .name("Lorem Ipsum")
        .email("lorem@email.com")
        .password("12345678")
        .confirm("12345678")
        .build();

    HttpEntity<SignupRequest> bodyAndHeaders = new HttpEntity<>(requestBody);

    ResponseEntity<ErrorDto> actualResponse = testRestTemplate.exchange(
        URI, HttpMethod.POST, bodyAndHeaders, new ParameterizedTypeReference<>() {});

    HttpStatus actualStatusCode = actualResponse.getStatusCode();
    ErrorDto actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(actualStatusCode).isEqualByComparingTo(HttpStatus.CONFLICT);
    Assertions.assertThat(actualResponseBody).isNotNull();
    System.out.println(actualResponseBody.getError());
    System.out.println(actualResponseBody.getMessage());
    // TODO: Após modificar o body em caso de status 4xx e 5xx, adicionar novas assertivas.
  }

}
