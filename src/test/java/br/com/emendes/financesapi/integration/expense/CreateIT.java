package br.com.emendes.financesapi.integration.expense;

import br.com.emendes.financesapi.controller.dto.error.FormErrorDto;
import br.com.emendes.financesapi.dto.request.ExpenseRequest;
import br.com.emendes.financesapi.dto.request.SignInRequest;
import br.com.emendes.financesapi.dto.response.ExpenseResponse;
import br.com.emendes.financesapi.dto.response.TokenResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("integration")
@DisplayName("Integration tests for POST /api/expenses")
class CreateIT {

  @Autowired
  private TestRestTemplate testRestTemplate;

  private final String URI = "/api/expenses";

  private HttpHeaders generateAuthorizationHeader() {
    HttpHeaders headers = new HttpHeaders();

    headers.add("Authorization", "Bearer " + signIn("lorem@email.com", "12345678"));

    return headers;
  }

  private String signIn(String email, String password) {
    HttpEntity<SignInRequest> requestBody = new HttpEntity<>(new SignInRequest(email, password));

    ResponseEntity<TokenResponse> response = testRestTemplate.exchange(
        "/api/auth/signin", HttpMethod.POST, requestBody, new ParameterizedTypeReference<>() {
        });

    TokenResponse body = response.getBody();

    if (body == null) {
      throw new IllegalArgumentException("Invalid email or password");
    }

    return body.getToken();
  }

  @Test
  @DisplayName("create must return 201 and ExpenseResponse when create successfully")
  @Sql(scripts = {"/sql/user/insert-user.sql"})
  void create_MustReturn201AndExpenseResponse_WhenCreateSuccessfully() {
    ExpenseRequest expenseRequest = ExpenseRequest.builder()
        .description("Aluguel")
        .value(new BigDecimal("1500.00"))
        .date("2023-02-05")
        .category("MORADIA")
        .build();

    HttpEntity<ExpenseRequest> requestEntity = new HttpEntity<>(expenseRequest, generateAuthorizationHeader());

    ResponseEntity<ExpenseResponse> actualResponse = testRestTemplate.exchange(
        URI, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<>() {
        });

    HttpStatus actualStatusCode = actualResponse.getStatusCode();
    ExpenseResponse actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(actualStatusCode).isEqualByComparingTo(HttpStatus.CREATED);
    Assertions.assertThat(actualResponseBody).isNotNull();
    Assertions.assertThat(actualResponseBody.getId()).isNotNull();
    Assertions.assertThat(actualResponseBody.getDescription()).isEqualTo("Aluguel");
    Assertions.assertThat(actualResponseBody.getDate()).isEqualTo("2023-02-05");
    Assertions.assertThat(actualResponseBody.getValue()).isEqualTo("1500.00");
    Assertions.assertThat(actualResponseBody.getCategory().name()).isEqualTo("MORADIA");
  }

  @Test
  @DisplayName("create must returns status 401 when user is not authenticated")
  void create_MustReturnStatus401_WhenUserIsNotAuthenticated() {
    ResponseEntity<Void> actualResponse = testRestTemplate.exchange(
        URI, HttpMethod.POST, null, new ParameterizedTypeReference<>() {
        });

    Assertions.assertThat(actualResponse.getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  @DisplayName("create must returns status 400 and List<FormErrorDto> when request body is invalid")
  @Sql(scripts = {"/sql/user/insert-user.sql"})
  void create_MustReturnStatus400AndListFormErrorDto_WhenRequestBodyIsInvalid() {
    ExpenseRequest expenseRequest = ExpenseRequest.builder()
        .description("")
        .value(new BigDecimal("150000000.00"))
        .date("05/02/2023")
        .category("  ")
        .build();

    HttpEntity<ExpenseRequest> requestEntity = new HttpEntity<>(expenseRequest, generateAuthorizationHeader());

    ResponseEntity<List<FormErrorDto>> actualResponse = testRestTemplate.exchange(
        URI, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<>() {
        });

    HttpStatus actualStatusCode = actualResponse.getStatusCode();
    List<FormErrorDto> actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(actualStatusCode).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    Assertions.assertThat(actualResponseBody).isNotNull().isNotEmpty().hasSize(5);
  }

}
