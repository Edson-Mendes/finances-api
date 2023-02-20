package br.com.emendes.financesapi.integration.authentication;

import br.com.emendes.financesapi.controller.dto.error.ErrorDto;
import br.com.emendes.financesapi.controller.dto.error.FormErrorDto;
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

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("integration")
@DisplayName("Integration tests for /api/auth/signin")
class SignInIT {

  @Autowired
  private TestRestTemplate testRestTemplate;

  private final String URI = "/api/auth/signin";

  @Test
  @DisplayName("sign in must returns status 200 and TokenResponse when sign in successfully")
  @Sql(scripts = {"/sql/user/insert-user.sql"})
  void SignIn_MustReturnsStatus200AndTokenResponse_WhenSignInSuccessfully() {
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
  @DisplayName("sign in must returns status 400 when bad credentials")
  @Sql(scripts = {"/sql/user/insert-user.sql"})
  void SignIn_MustReturnsStatus400_WhenBadCredentials() {
    SignInRequest requestBody = SignInRequest.builder()
        .email("lorem@email.com")
        .password("wrongpassword")
        .build();
    HttpEntity<SignInRequest> bodyAndHeaders = new HttpEntity<>(requestBody);

    ResponseEntity<ErrorDto> actualResponse = testRestTemplate.exchange(
        URI, HttpMethod.POST, bodyAndHeaders, new ParameterizedTypeReference<>() {});

    HttpStatus actualStatusCode = actualResponse.getStatusCode();
    ErrorDto actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(actualStatusCode).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    Assertions.assertThat(actualResponseBody).isNotNull();
    System.out.println(actualResponseBody.getError());
    System.out.println(actualResponseBody.getMessage());
    // TODO: Após modificar o body em caso de status 4xx e 5xx, adicionar novas assertivas.
  }

  @Test
  @DisplayName("sign in must returns status 400 when request body is invalid")
  void SignIn_MustReturnsStatus400_WhenRequestBodyIsInvalid() {
    SignInRequest requestBody = SignInRequest.builder()
        .email("invalidemailcom")
        .password(null)
        .build();
    HttpEntity<SignInRequest> bodyAndHeaders = new HttpEntity<>(requestBody);

    ResponseEntity<List<FormErrorDto>> actualResponse = testRestTemplate.exchange(
        URI, HttpMethod.POST, bodyAndHeaders, new ParameterizedTypeReference<>() {});

    HttpStatus actualStatusCode = actualResponse.getStatusCode();
    List<FormErrorDto> actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(actualStatusCode).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    Assertions.assertThat(actualResponseBody).isNotNull();
    // TODO: Após modificar o body em caso de status 4xx e 5xx, adicionar novas assertivas.
  }

}
