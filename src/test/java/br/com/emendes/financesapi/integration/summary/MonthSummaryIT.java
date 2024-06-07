package br.com.emendes.financesapi.integration.summary;

import br.com.emendes.financesapi.dto.problem.ProblemDetail;
import br.com.emendes.financesapi.dto.response.SummaryResponse;
import br.com.emendes.financesapi.util.component.SignIn;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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

import static br.com.emendes.financesapi.util.constant.SqlPath.INSERT_INCOMES_EXPENSES_SQL_PATH;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("integration")
@DisplayName("Integration tests for GET /api/summaries/{year}/{month}")
class MonthSummaryIT {

  @Autowired
  private TestRestTemplate testRestTemplate;
  @Autowired
  private SignIn signIn;

  private final String URI = "/api/summaries";
  private final String EMAIL = "lorem@email.com";
  private final String PASSWORD = "12345678";

  @Test
  @DisplayName("monthSummary must return status 200 and SummaryResponse read monthSummary successfully")
  @Sql(scripts = {INSERT_INCOMES_EXPENSES_SQL_PATH})
  void monthSummary_MustReturnStatus200AndSummaryResponse_WhenReadMonthSummarySuccessfully() {
    HttpEntity<Void> requestEntity = new HttpEntity<>(signIn.generateAuthorizationHeader(EMAIL, PASSWORD));

    ResponseEntity<SummaryResponse> actualResponse = testRestTemplate.exchange(
        URI + "/2023/02", HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {
        });

    HttpStatusCode actualStatusCode = actualResponse.getStatusCode();
    SummaryResponse actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(actualStatusCode).isEqualTo(HttpStatusCode.valueOf(200));
    Assertions.assertThat(actualResponseBody).isNotNull();
    Assertions.assertThat(actualResponseBody.getIncomeTotalValue()).isEqualTo("3500.00");
    Assertions.assertThat(actualResponseBody.getExpenseTotalValue()).isEqualTo("3055.00");
    Assertions.assertThat(actualResponseBody.getFinalBalance()).isEqualTo("445.00");
    Assertions.assertThat(actualResponseBody.getValuesByCategory()).hasSize(5);
  }

  @Test
  @DisplayName("monthSummary must return status 404 and ProblemDetail when summary no exists")
  @Sql(scripts = {INSERT_INCOMES_EXPENSES_SQL_PATH})
  void monthSummary_MustReturnStatus404AndProblemDetail_WhenSummaryNoExists() {
    HttpEntity<Void> requestEntity = new HttpEntity<>(signIn.generateAuthorizationHeader(EMAIL, PASSWORD));

    ResponseEntity<ProblemDetail> actualResponse = testRestTemplate.exchange(
        URI + "/2023/08", HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {
        });

    HttpStatusCode actualStatusCode = actualResponse.getStatusCode();
    ProblemDetail actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(actualStatusCode).isEqualTo(HttpStatusCode.valueOf(404));
    Assertions.assertThat(actualResponseBody).isNotNull();
    Assertions.assertThat(actualResponseBody.getTitle()).isEqualTo("Entity not found");
    Assertions.assertThat(actualResponseBody.getDetail()).isEqualTo("Has no expenses or incomes for AUGUST 2023");
  }

  @Test
  @DisplayName("monthSummary must return status 401 when user is not authenticated")
  void monthSummary_MustReturnStatus401_WhenUserIsNotAuthenticated() {
    ResponseEntity<Void> actualResponse = testRestTemplate.exchange(
        URI + "/2023/01", HttpMethod.GET, null, new ParameterizedTypeReference<>() {
        });

    Assertions.assertThat(actualResponse.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(401));
  }

  @Test
  @DisplayName("monthSummary must return 400 and ProblemDetail when can not parse year")
  @Sql(scripts = {INSERT_INCOMES_EXPENSES_SQL_PATH})
  void monthSummary_MustReturn400AndProblemDetail_WhenCanNotParseYear() {
    HttpEntity<Void> requestEntity = new HttpEntity<>(signIn.generateAuthorizationHeader(EMAIL, PASSWORD));

    ResponseEntity<ProblemDetail> actualResponse = testRestTemplate.exchange(
        URI + "/2o23/2",
        HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {
        });

    HttpStatusCode actualStatusCode = actualResponse.getStatusCode();
    ProblemDetail actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(actualStatusCode).isEqualTo(HttpStatusCode.valueOf(400));
    Assertions.assertThat(actualResponseBody).isNotNull();
    Assertions.assertThat(actualResponseBody.getTitle()).isEqualTo("Type mismatch");
    Assertions.assertThat(actualResponseBody.getDetail())
        .isEqualTo("An error occurred trying to cast String to Number");
  }

  @Test
  @DisplayName("monthSummary must return 400 and ProblemDetail when can not parse month")
  @Sql(scripts = {INSERT_INCOMES_EXPENSES_SQL_PATH})
  void monthSummary_MustReturn400AndProblemDetail_WhenCanNotParseMonth() {
    HttpEntity<Void> requestEntity = new HttpEntity<>(signIn.generateAuthorizationHeader(EMAIL, PASSWORD));

    ResponseEntity<ProblemDetail> actualResponse = testRestTemplate.exchange(
        URI + "/2023/1o",
        HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {
        });

    HttpStatusCode actualStatusCode = actualResponse.getStatusCode();
    ProblemDetail actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(actualStatusCode).isEqualTo(HttpStatusCode.valueOf(400));
    Assertions.assertThat(actualResponseBody).isNotNull();
    Assertions.assertThat(actualResponseBody.getTitle()).isEqualTo("Type mismatch");
    Assertions.assertThat(actualResponseBody.getDetail()).contains("An error occurred trying to cast String to Number");
  }

  @ParameterizedTest
  @CsvSource(value = {"/1969/3, year must be equals or greater than 1970", "/2100/3, year must be equals or less than 2099"})
  @DisplayName("monthSummary must return 400 and ProblemDetail when year is out of range")
  @Sql(scripts = {INSERT_INCOMES_EXPENSES_SQL_PATH})
  void monthSummary_MustReturn400AndProblemDetail_WhenYearIsOutOfRange(String input, String expectedDetail) {
    HttpEntity<Void> requestEntity = new HttpEntity<>(signIn.generateAuthorizationHeader(EMAIL, PASSWORD));

    ResponseEntity<ProblemDetail> actualResponse = testRestTemplate.exchange(
        URI + input,
        HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {
        });

    HttpStatusCode actualStatusCode = actualResponse.getStatusCode();
    ProblemDetail actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(actualStatusCode).isEqualTo(HttpStatusCode.valueOf(400));
    Assertions.assertThat(actualResponseBody).isNotNull();
    Assertions.assertThat(actualResponseBody.getTitle()).isEqualTo("Invalid arguments");
    Assertions.assertThat(actualResponseBody.getDetail()).isEqualTo(expectedDetail);
  }

  @ParameterizedTest
  @CsvSource(value = {"/2023/13, month must be equals or less than 12", "/2023/0, month must be equals or greater than 1"})
  @DisplayName("monthSummary must return 400 and ProblemDetail when month is out of range")
  @Sql(scripts = {INSERT_INCOMES_EXPENSES_SQL_PATH})
  void monthSummary_MustReturn400AndProblemDetail_WhenMonthIsOutOfRange(String input, String expectedDetail) {
    HttpEntity<Void> requestEntity = new HttpEntity<>(signIn.generateAuthorizationHeader(EMAIL, PASSWORD));

    ResponseEntity<ProblemDetail> actualResponse = testRestTemplate.exchange(
        URI + input,
        HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {
        });

    HttpStatusCode actualStatusCode = actualResponse.getStatusCode();
    ProblemDetail actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(actualStatusCode).isEqualTo(HttpStatusCode.valueOf(400));
    Assertions.assertThat(actualResponseBody).isNotNull();
    Assertions.assertThat(actualResponseBody.getTitle()).isEqualTo("Invalid arguments");
    Assertions.assertThat(actualResponseBody.getDetail()).isEqualTo(expectedDetail);
  }

}
