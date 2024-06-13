package br.com.emendes.financesapi.integration.expense;

import br.com.emendes.financesapi.dto.problem.ProblemDetail;
import br.com.emendes.financesapi.dto.problem.ValidationProblemDetail;
import br.com.emendes.financesapi.dto.request.ExpenseRequest;
import br.com.emendes.financesapi.dto.response.ExpenseResponse;
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
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;

import static br.com.emendes.financesapi.util.constant.AuthenticationConstant.USER_EMAIL;
import static br.com.emendes.financesapi.util.constant.AuthenticationConstant.USER_PASSWORD;
import static br.com.emendes.financesapi.util.constant.SqlPath.INSERT_EXPENSE_SQL_PATH;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("integration")
@DisplayName("Integration tests for PUT /api/expenses/{expenseId}")
class UpdateIT {

  @Autowired
  private TestRestTemplate testRestTemplate;
  @Autowired
  private SignIn signIn;

  private final String URI = "/api/expenses";

  @Test
  @DisplayName("update must return status 200 and ExpenseResponse when update successfully")
  @Sql(scripts = {INSERT_EXPENSE_SQL_PATH})
  void update_MustReturnStatus200AndExpenseResponse_WhenUpdateSuccessfully() {
    ExpenseRequest expenseRequest = ExpenseRequest.builder()
        .description("Aluguel")
        .value(new BigDecimal("1800.00"))
        .date("2023-02-05")
        .category("MORADIA")
        .build();

    HttpEntity<ExpenseRequest> requestEntity =
        new HttpEntity<>(expenseRequest, signIn.generateAuthorizationHeader(USER_EMAIL, USER_PASSWORD));

    ResponseEntity<ExpenseResponse> actualResponse = testRestTemplate.exchange(
        URI + "/1", HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<>() {
        });

    HttpStatusCode statusCode = actualResponse.getStatusCode();
    ExpenseResponse actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(statusCode).isEqualTo(HttpStatusCode.valueOf(200));
    Assertions.assertThat(actualResponseBody).isNotNull();
    Assertions.assertThat(actualResponseBody.getId()).isEqualTo(1L);
    Assertions.assertThat(actualResponseBody.getDescription()).isEqualTo("Aluguel");
    Assertions.assertThat(actualResponseBody.getValue()).isEqualTo("1800.00");
  }

  @Test
  @DisplayName("update must return status 400 and ValidationProblemDetail when body is invalid")
  @Sql(scripts = {INSERT_EXPENSE_SQL_PATH})
  void update_MustReturnStatus400AndValidationProblemDetail_WhenBodyIsInvalid() {
    ExpenseRequest expenseRequest = ExpenseRequest.builder()
        .description("")
        .value(new BigDecimal("-1800.00"))
        .date("2023/02/05")
        .category("  ")
        .build();

    HttpEntity<ExpenseRequest> requestEntity =
        new HttpEntity<>(expenseRequest, signIn.generateAuthorizationHeader(USER_EMAIL, USER_PASSWORD));

    ResponseEntity<ValidationProblemDetail> actualResponse = testRestTemplate.exchange(
        URI + "/1", HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<>() {
        });

    HttpStatusCode statusCode = actualResponse.getStatusCode();
    ValidationProblemDetail actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(statusCode).isEqualTo(HttpStatusCode.valueOf(400));
    Assertions.assertThat(actualResponseBody).isNotNull();
    Assertions.assertThat(actualResponseBody.getTitle()).isEqualTo("Invalid fields");
    Assertions.assertThat(actualResponseBody.getDetail()).isEqualTo("Some fields are invalid");
    Assertions.assertThat(actualResponseBody.getFields()).contains("description", "value", "date", "category");
    Assertions.assertThat(actualResponseBody.getMessages())
        .contains("description must not be null or blank", "Invalid date",
            "value must be positive", "category must not be null or blank");
  }

  @Test
  @DisplayName("update must return status 400 and ProblemDetail when id is invalid")
  @Sql(scripts = {INSERT_EXPENSE_SQL_PATH})
  void update_MustReturnStatus400AndProblemDetail_WhenIdIsInvalid() {
    ExpenseRequest expenseRequest = ExpenseRequest.builder()
        .description("Aluguel")
        .value(new BigDecimal("1800.00"))
        .date("2023-02-05")
        .category("MORADIA")
        .build();

    HttpEntity<ExpenseRequest> requestEntity =
        new HttpEntity<>(expenseRequest, signIn.generateAuthorizationHeader(USER_EMAIL, USER_PASSWORD));

    ResponseEntity<ProblemDetail> actualResponse = testRestTemplate.exchange(
        URI + "/1o", HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<>() {
        });

    HttpStatusCode statusCode = actualResponse.getStatusCode();
    ProblemDetail actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(statusCode).isEqualTo(HttpStatusCode.valueOf(400));
    Assertions.assertThat(actualResponseBody).isNotNull();
    Assertions.assertThat(actualResponseBody.getTitle()).isEqualTo("Type mismatch");
    Assertions.assertThat(actualResponseBody.getDetail())
        .isEqualTo("An error occurred trying to cast String to Number");
  }

  @Test
  @DisplayName("update must return status 401 when user is not authenticated")
  void update_MustReturnStatus401_WhenUserIsNotAuthenticated() {
    ResponseEntity<Void> actualResponse = testRestTemplate.exchange(
        URI + "/1", HttpMethod.PUT, null, new ParameterizedTypeReference<>() {
        });

    HttpStatusCode statusCode = actualResponse.getStatusCode();

    Assertions.assertThat(statusCode).isEqualTo(HttpStatusCode.valueOf(401));
  }

  @Test
  @DisplayName("update must return status 404 and ProblemDetail when id no exists")
  @Sql(scripts = {INSERT_EXPENSE_SQL_PATH})
  void update_MustReturnStatus404AndProblemDetail_WhenIdNoExists() {
    ExpenseRequest expenseRequest = ExpenseRequest.builder()
        .description("Aluguel")
        .value(new BigDecimal("1800.00"))
        .date("2023-02-05")
        .category("MORADIA")
        .build();

    HttpEntity<ExpenseRequest> requestEntity =
        new HttpEntity<>(expenseRequest, signIn.generateAuthorizationHeader(USER_EMAIL, USER_PASSWORD));

    ResponseEntity<ProblemDetail> actualResponse = testRestTemplate.exchange(
        URI + "/10000", HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<>() {
        });

    HttpStatusCode statusCode = actualResponse.getStatusCode();
    ProblemDetail actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(statusCode).isEqualTo(HttpStatusCode.valueOf(404));
    Assertions.assertThat(actualResponseBody).isNotNull();
    Assertions.assertThat(actualResponseBody.getTitle()).isEqualTo("Entity not found");
    Assertions.assertThat(actualResponseBody.getDetail()).isEqualTo("Expense not found");
  }

}
