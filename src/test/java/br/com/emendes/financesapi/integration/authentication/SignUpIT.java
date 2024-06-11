package br.com.emendes.financesapi.integration.authentication;

import br.com.emendes.financesapi.dto.problem.ProblemDetail;
import br.com.emendes.financesapi.dto.problem.ValidationProblemDetail;
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
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static br.com.emendes.financesapi.util.constant.SqlPath.INSERT_USER_SQL_PATH;

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
        URI, HttpMethod.POST, bodyAndHeaders, new ParameterizedTypeReference<>() {
        });

    HttpStatusCode actualStatusCode = actualResponse.getStatusCode();
    UserResponse actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(actualStatusCode).isEqualTo(HttpStatusCode.valueOf(201));
    Assertions.assertThat(actualResponseBody).isNotNull();
    Assertions.assertThat(actualResponseBody.getId()).isNotNull().isPositive();
    Assertions.assertThat(actualResponseBody.getName()).isEqualTo("Lorem Ipsum");
    Assertions.assertThat(actualResponseBody.getEmail()).isEqualTo("lorem@email.com");
  }

  @Test
  @DisplayName("sign up must returns status 400 and ValidationProblemDetail when request body is invalid")
  void signUp_MustReturnsStatus400AndValidationProblemDetail_WhenRequestBodyIsInvalid() {
    SignupRequest requestBody = SignupRequest.builder()
        .name(null)
        .email("invalidemailcom")
        .password("   ")
        .confirm(null)
        .build();

    HttpEntity<SignupRequest> bodyAndHeaders = new HttpEntity<>(requestBody);

    ResponseEntity<ValidationProblemDetail> actualResponse = testRestTemplate.exchange(
        URI, HttpMethod.POST, bodyAndHeaders, new ParameterizedTypeReference<>() {
        });

    HttpStatusCode actualStatusCode = actualResponse.getStatusCode();
    ValidationProblemDetail actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(actualStatusCode).isEqualTo(HttpStatusCode.valueOf(400));
    Assertions.assertThat(actualResponseBody).isNotNull();
    Assertions.assertThat(actualResponseBody.getTitle()).isEqualTo("Invalid fields");
    Assertions.assertThat(actualResponseBody.getDetail()).isEqualTo("Some fields are invalid");
    Assertions.assertThat(actualResponseBody.getFields()).contains("name", "email", "password", "confirm");
    Assertions.assertThat(actualResponseBody.getMessages()).contains(
        "name must not be null or blank", "must be a well formed email",
        "password must not be null or blank", "confirm must not be null or blank");
  }

  @Test
  @DisplayName("sign up must returns status 400 and ProblemDetail when password and confirm do not match")
  void signUp_MustReturnsStatus400AndProblemDetail_WhenPasswordAndConfirmDoNotMatch() {
    SignupRequest requestBody = SignupRequest.builder()
        .name("Lorem Ipsum")
        .email("lorem@email.com")
        .password("12345678")
        .confirm("1234567890")
        .build();

    HttpEntity<SignupRequest> bodyAndHeaders = new HttpEntity<>(requestBody);

    ResponseEntity<ProblemDetail> actualResponse = testRestTemplate.exchange(
        URI, HttpMethod.POST, bodyAndHeaders, new ParameterizedTypeReference<>() {
        });

    HttpStatusCode actualStatusCode = actualResponse.getStatusCode();
    ProblemDetail actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(actualStatusCode).isEqualTo(HttpStatusCode.valueOf(400));
    Assertions.assertThat(actualResponseBody).isNotNull();
    Assertions.assertThat(actualResponseBody.getTitle()).isEqualTo("Passwords do not match");
    Assertions.assertThat(actualResponseBody.getDetail()).isEqualTo("Password and confirm do not match");
  }

  @Test
  @DisplayName("sign up must returns status 409 and ProblemDetail when informed email is already in use")
  @Sql(scripts = {INSERT_USER_SQL_PATH})
  void signUp_MustReturnsStatus409AndProblemDetail_WhenInformedEmailIsAlreadyInUse() {
    SignupRequest requestBody = SignupRequest.builder()
        .name("Lorem Ipsum")
        .email("lorem@email.com")
        .password("12345678")
        .confirm("12345678")
        .build();

    HttpEntity<SignupRequest> bodyAndHeaders = new HttpEntity<>(requestBody);

    ResponseEntity<ProblemDetail> actualResponse = testRestTemplate.exchange(
        URI, HttpMethod.POST, bodyAndHeaders, new ParameterizedTypeReference<>() {
        });

    HttpStatusCode actualStatusCode = actualResponse.getStatusCode();
    ProblemDetail actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(actualStatusCode).isEqualTo(HttpStatusCode.valueOf(409));
    Assertions.assertThat(actualResponseBody).isNotNull();
    Assertions.assertThat(actualResponseBody.getTitle()).isEqualTo("Data conflict");
    Assertions.assertThat(actualResponseBody.getDetail()).isEqualTo("Email is already in use");
  }

}
