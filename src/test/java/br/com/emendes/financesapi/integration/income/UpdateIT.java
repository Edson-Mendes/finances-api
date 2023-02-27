package br.com.emendes.financesapi.integration.income;

import br.com.emendes.financesapi.dto.problem.ProblemDetail;
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

import static br.com.emendes.financesapi.util.constant.SqlPath.INSERT_INCOME_SQL_PATH;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("integration")
@DisplayName("Integration tests for PUT /api/incomes/{incomeId}")
class UpdateIT {

  @Autowired
  private TestRestTemplate testRestTemplate;
  @Autowired
  private SignIn signIn;

  private final String URI = "/api/incomes";
  private final String EMAIL = "lorem@email.com";
  private final String PASSWORD = "12345678";

  @Test
  @DisplayName("update must return status 200 and IncomeResponse when update successfully")
  @Sql(scripts = {INSERT_INCOME_SQL_PATH})
  void update_MustReturnStatus200AndIncomeResponse_WhenUpdateSuccessfully() {
    IncomeRequest incomeRequest = IncomeRequest.builder()
        .description("Salário")
        .value(new BigDecimal("3000.00"))
        .date("2023-02-05")
        .build();

    HttpEntity<IncomeRequest> requestEntity =
        new HttpEntity<>(incomeRequest, signIn.generateAuthorizationHeader(EMAIL, PASSWORD));

    ResponseEntity<IncomeResponse> actualResponse = testRestTemplate.exchange(
        URI + "/1", HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = actualResponse.getStatusCode();
    IncomeResponse actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.OK);
    Assertions.assertThat(actualResponseBody).isNotNull();
    Assertions.assertThat(actualResponseBody.getId()).isEqualTo(1L);
    Assertions.assertThat(actualResponseBody.getDescription()).isEqualTo("Salário");
    Assertions.assertThat(actualResponseBody.getValue()).isEqualTo("3000.00");
  }

  @Test
  @DisplayName("update must return status 400 and ValidationProblemDetail when body is invalid")
  @Sql(scripts = {INSERT_INCOME_SQL_PATH})
  void update_MustReturnStatus400AndValidationProblemDetail_WhenBodyIsInvalid() {
    IncomeRequest incomeRequest = IncomeRequest.builder()
        .description("")
        .value(new BigDecimal("-2500.00"))
        .date("2023/02/05")
        .build();

    HttpEntity<IncomeRequest> requestEntity =
        new HttpEntity<>(incomeRequest, signIn.generateAuthorizationHeader(EMAIL, PASSWORD));

    ResponseEntity<ValidationProblemDetail> actualResponse = testRestTemplate.exchange(
        URI + "/1", HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = actualResponse.getStatusCode();
    ValidationProblemDetail actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    Assertions.assertThat(actualResponseBody).isNotNull();
    Assertions.assertThat(actualResponseBody.getTitle()).isEqualTo("Invalid fields");
    Assertions.assertThat(actualResponseBody.getDetail()).isEqualTo("Some fields are invalid");
    Assertions.assertThat(actualResponseBody.getFields()).contains("description", "value", "date");
    Assertions.assertThat(actualResponseBody.getMessages())
        .contains("description must not be null or blank", "Invalid date", "value must be positive");
  }

  @Test
  @DisplayName("update must return status 400 and ProblemDetail when id is invalid")
  @Sql(scripts = {INSERT_INCOME_SQL_PATH})
  void update_MustReturnStatus400AndProblemDetail_WhenIdIsInvalid() {
    IncomeRequest incomeRequest = IncomeRequest.builder()
        .description("Salário")
        .value(new BigDecimal("3000.00"))
        .date("2023-02-05")
        .build();

    HttpEntity<IncomeRequest> requestEntity =
        new HttpEntity<>(incomeRequest, signIn.generateAuthorizationHeader(EMAIL, PASSWORD));

    ResponseEntity<ProblemDetail> actualResponse = testRestTemplate.exchange(
        URI + "/1o", HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = actualResponse.getStatusCode();
    ProblemDetail actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    Assertions.assertThat(actualResponseBody).isNotNull();
    Assertions.assertThat(actualResponseBody.getTitle()).isEqualTo("Type mismatch");
    Assertions.assertThat(actualResponseBody.getDetail())
        .isEqualTo("An error occurred trying to cast String to Number");
  }

  @Test
  @DisplayName("update must return status 401 when user is not authenticated")
  void update_MustReturnStatus401_WhenUserIsNotAuthenticated() {
    ResponseEntity<Void> actualResponse = testRestTemplate.exchange(
        URI + "/1", HttpMethod.PUT, null, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = actualResponse.getStatusCode();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  @DisplayName("update must return status 404 and ProblemDetail when id no exists")
  @Sql(scripts = {INSERT_INCOME_SQL_PATH})
  void update_MustReturnStatus404AndProblemDetail_WhenIdNoExists() {
    IncomeRequest incomeRequest = IncomeRequest.builder()
        .description("Salário")
        .value(new BigDecimal("1800.00"))
        .date("2023-02-05")
        .build();

    HttpEntity<IncomeRequest> requestEntity =
        new HttpEntity<>(incomeRequest, signIn.generateAuthorizationHeader(EMAIL, PASSWORD));

    ResponseEntity<ProblemDetail> actualResponse = testRestTemplate.exchange(
        URI + "/10000", HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = actualResponse.getStatusCode();
    ProblemDetail actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.NOT_FOUND);
    Assertions.assertThat(actualResponseBody).isNotNull();
    Assertions.assertThat(actualResponseBody.getTitle()).isEqualTo("Entity not found");
    Assertions.assertThat(actualResponseBody.getDetail()).isEqualTo("Income not found");
  }

}
