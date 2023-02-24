package br.com.emendes.financesapi.integration.income;

import br.com.emendes.financesapi.dto.problem.ValidationProblemDetail;
import br.com.emendes.financesapi.dto.request.IncomeRequest;
import br.com.emendes.financesapi.dto.response.IncomeResponse;
import br.com.emendes.financesapi.util.component.SignIn;
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

import java.math.BigDecimal;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("integration")
@DisplayName("Integration tests for POST /api/incomes")
class CreateIT {

  @Autowired
  private TestRestTemplate testRestTemplate;
  @Autowired
  private SignIn signIn;

  private final String URI = "/api/incomes";
  private final String EMAIL = "lorem@email.com";
  private final String PASSWORD = "12345678";

  @Test
  @DisplayName("create must return 201 and IncomeResponse when create successfully")
  @Sql(scripts = {"/sql/user/insert-user.sql"})
  void create_MustReturn201AndIncomeResponse_WhenCreateSuccessfully() {
    IncomeRequest incomeRequest = IncomeRequest.builder()
        .description("Salário")
        .value(new BigDecimal("2500.00"))
        .date("2023-02-05")
        .build();

    HttpEntity<IncomeRequest> requestEntity = new HttpEntity<>(
        incomeRequest, signIn.generateAuthorizationHeader(EMAIL, PASSWORD));

    ResponseEntity<IncomeResponse> actualResponse = testRestTemplate.exchange(
        URI, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus actualStatusCode = actualResponse.getStatusCode();
    IncomeResponse actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(actualStatusCode).isEqualByComparingTo(HttpStatus.CREATED);
    Assertions.assertThat(actualResponseBody).isNotNull();
    Assertions.assertThat(actualResponseBody.getId()).isNotNull();
    Assertions.assertThat(actualResponseBody.getDescription()).isEqualTo("Salário");
    Assertions.assertThat(actualResponseBody.getDate()).isEqualTo("2023-02-05");
    Assertions.assertThat(actualResponseBody.getValue()).isEqualTo("2500.00");
  }

  @Test
  @DisplayName("create must returns status 401 when user is not authenticated")
  void create_MustReturnStatus401_WhenUserIsNotAuthenticated() {
    IncomeRequest incomeRequest = IncomeRequest.builder()
        .description("Salário")
        .value(new BigDecimal("2500.00"))
        .date("2023-02-05")
        .build();

    HttpEntity<IncomeRequest> requestEntity = new HttpEntity<>(incomeRequest);

    ResponseEntity<Void> actualResponse = testRestTemplate.exchange(
        URI, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<>() {});

    Assertions.assertThat(actualResponse.getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  @DisplayName("create must returns status 400 and ValidationProblemDetail when request body is invalid")
  @Sql(scripts = {"/sql/user/insert-user.sql"})
  void create_MustReturnStatus400AndValidationProblemDetail_WhenRequestBodyIsInvalid() {
    IncomeRequest incomeRequest = IncomeRequest.builder()
        .description("")
        .value(new BigDecimal("150000000.00"))
        .date("05/02/2023")
        .build();

    HttpEntity<IncomeRequest> requestEntity = new HttpEntity<>(
        incomeRequest, signIn.generateAuthorizationHeader(EMAIL, PASSWORD));

    ResponseEntity<ValidationProblemDetail> actualResponse = testRestTemplate.exchange(
        URI, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus actualStatusCode = actualResponse.getStatusCode();
    ValidationProblemDetail actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(actualStatusCode).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    Assertions.assertThat(actualResponseBody).isNotNull();
    Assertions.assertThat(actualResponseBody.getTitle()).isEqualTo("Invalid fields");
    Assertions.assertThat(actualResponseBody.getDetail()).isEqualTo("Some fields are invalid");
    Assertions.assertThat(actualResponseBody.getFields()).contains("description", "value", "date");
    Assertions.assertThat(actualResponseBody.getMessages()).contains(
        "description must not be null or blank",
        "Integer part must be max 6 digits and fraction part must be max 2 digits",
        "Invalid date");
  }

}
