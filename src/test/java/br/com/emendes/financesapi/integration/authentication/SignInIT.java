package br.com.emendes.financesapi.integration.authentication;

import br.com.emendes.financesapi.dto.problem.ProblemDetail;
import br.com.emendes.financesapi.dto.problem.ValidationProblemDetail;
import br.com.emendes.financesapi.dto.request.SignInRequest;
import br.com.emendes.financesapi.dto.response.TokenResponse;
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

import static br.com.emendes.financesapi.util.constant.SqlPath.INSERT_USER_SQL_PATH;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("integration")
@DisplayName("Integration tests for POST /api/auth/signin")
class SignInIT {

  @Autowired
  private TestRestTemplate testRestTemplate;

  private final String URI = "/api/auth/signin";

  @Test
  @DisplayName("sign in must returns status 200 and TokenResponse when sign in successfully")
  @Sql(scripts = {INSERT_USER_SQL_PATH})
  void signIn_MustReturnStatus200AndTokenResponse_WhenSignInSuccessfully() {
    SignInRequest requestBody = SignInRequest.builder()
        .email("lorem@email.com")
        .password("12345678")
        .build();
    HttpEntity<SignInRequest> bodyAndHeaders = new HttpEntity<>(requestBody);

    ResponseEntity<TokenResponse> actualResponse = testRestTemplate.exchange(
        URI, HttpMethod.POST, bodyAndHeaders, new ParameterizedTypeReference<>() {});

    HttpStatus actualStatusCode = actualResponse.getStatusCode();
    TokenResponse actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(actualStatusCode).isEqualByComparingTo(HttpStatus.OK);
    Assertions.assertThat(actualResponseBody).isNotNull();
    Assertions.assertThat(actualResponseBody.getType()).isEqualTo("Bearer");
    Assertions.assertThat(actualResponseBody.getToken()).isNotBlank();
  }

  @Test
  @DisplayName("sign in must returns status 400 and ProblemDetail when bad credentials")
  @Sql(scripts = {INSERT_USER_SQL_PATH})
  void signIn_MustReturnStatus400AndProblemDetail_WhenBadCredentials() {
    SignInRequest requestBody = SignInRequest.builder()
        .email("lorem@email.com")
        .password("wrongpassword")
        .build();
    HttpEntity<SignInRequest> bodyAndHeaders = new HttpEntity<>(requestBody);

    ResponseEntity<ProblemDetail> actualResponse = testRestTemplate.exchange(
        URI, HttpMethod.POST, bodyAndHeaders, new ParameterizedTypeReference<>() {});

    HttpStatus actualStatusCode = actualResponse.getStatusCode();
    ProblemDetail actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(actualStatusCode).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    Assertions.assertThat(actualResponseBody).isNotNull();
    Assertions.assertThat(actualResponseBody.getTitle()).isEqualTo("Bad credentials");
    Assertions.assertThat(actualResponseBody.getDetail()).isEqualTo("Invalid email or password");
  }

  @Test
  @DisplayName("sign in must returns status 400 and ValidationProblemDetail when request body is invalid")
  void signIn_MustReturnStatus400AndValidationProblemDetail_WhenRequestBodyIsInvalid() {
    SignInRequest requestBody = SignInRequest.builder()
        .email("invalidemailcom")
        .password(null)
        .build();
    HttpEntity<SignInRequest> bodyAndHeaders = new HttpEntity<>(requestBody);

    ResponseEntity<ValidationProblemDetail> actualResponse = testRestTemplate.exchange(
        URI, HttpMethod.POST, bodyAndHeaders, new ParameterizedTypeReference<>() {});

    HttpStatus actualStatusCode = actualResponse.getStatusCode();
    ValidationProblemDetail actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(actualStatusCode).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    Assertions.assertThat(actualResponseBody).isNotNull();
    Assertions.assertThat(actualResponseBody.getTitle()).isEqualTo("Invalid fields");
    Assertions.assertThat(actualResponseBody.getDetail()).isEqualTo("Some fields are invalid");
    Assertions.assertThat(actualResponseBody.getFields()).contains("email", "password");
    Assertions.assertThat(actualResponseBody.getMessages())
        .contains("must be a well formed email", "password must not be null or blank");
  }

}
