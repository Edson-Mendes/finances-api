package br.com.emendes.financesapi.integration.expense;

import br.com.emendes.financesapi.dto.problem.ProblemDetail;
import br.com.emendes.financesapi.dto.response.ExpenseResponse;
import br.com.emendes.financesapi.util.component.SignIn;
import br.com.emendes.financesapi.util.wrapper.PageableResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
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
@DisplayName("Integration tests for GET /api/expenses/{year}/{month}")
class ReadByYearAndMonthIT {

  @Autowired
  private TestRestTemplate testRestTemplate;
  @Autowired
  private SignIn signIn;

  private final String URI = "/api/expenses";
  private final String EMAIL = "lorem@email.com";
  private final String PASSWORD = "12345678";

  @Test
  @DisplayName("readByYearAndMonth must return status 200 and Page<ExpenseResponse> when read successfully")
  @Sql(scripts = {"/sql/expense/insert-expense.sql"})
  void readByYearAndMonth_MustReturnStatus200AndPageExpenseResponse_WhenReadSuccessfully() {
    HttpEntity<Void> requestEntity = new HttpEntity<>(signIn.generateAuthorizationHeader(EMAIL, PASSWORD));

    ResponseEntity<PageableResponse<ExpenseResponse>> actualResponse = testRestTemplate.exchange(
        URI + "/2023/2", HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus actualStatusCode = actualResponse.getStatusCode();
    Page<ExpenseResponse> actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(actualStatusCode).isEqualByComparingTo(HttpStatus.OK);
    Assertions.assertThat(actualResponseBody).isNotNull().hasSize(2);
    List<String> actualDescriptionList = actualResponseBody.stream().map(ExpenseResponse::getDescription).toList();

    Assertions.assertThat(actualDescriptionList).contains("Aluguel", "Supermercado");
  }

  @Test
  @DisplayName("readByYearAndMonth must return status 200 and empty page when read by year and month and page one")
  @Sql(scripts = {"/sql/expense/insert-expense.sql"})
  void readByYearAndMonth_MustReturnStatus200AndEmptyPage_WhenReadByYearAndMonthAndPageOne() {
    HttpEntity<Void> requestEntity = new HttpEntity<>(signIn.generateAuthorizationHeader(EMAIL, PASSWORD));

    ResponseEntity<PageableResponse<ExpenseResponse>> actualResponse = testRestTemplate.exchange(
        URI + "/2023/2?page=1", HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus actualStatusCode = actualResponse.getStatusCode();
    Page<ExpenseResponse> actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(actualStatusCode).isEqualByComparingTo(HttpStatus.OK);
    Assertions.assertThat(actualResponseBody).isNotNull().isEmpty();
    Assertions.assertThat(actualResponseBody.getTotalPages()).isEqualTo(1);
    Assertions.assertThat(actualResponseBody.getTotalElements()).isEqualTo(2L);
  }

  @Test
  @DisplayName("readByYearAndMonth must return status 400 and ProblemDetail when year can not be parsed")
  @Sql(scripts = {"/sql/expense/insert-expense.sql"})
  void readByYearAndMonth_MustReturnStatus400AndProblemDetail_WhenYearCanNotBeParsed() {
    HttpEntity<Void> requestEntity = new HttpEntity<>(signIn.generateAuthorizationHeader(EMAIL, PASSWORD));

    ResponseEntity<ProblemDetail> actualResponse = testRestTemplate.exchange(
        URI + "/2023/ll", HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus actualStatusCode = actualResponse.getStatusCode();
    ProblemDetail actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(actualStatusCode).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    Assertions.assertThat(actualResponseBody).isNotNull();
    Assertions.assertThat(actualResponseBody.getTitle()).isEqualTo("Type mismatch");
    Assertions.assertThat(actualResponseBody.getDetail())
        .contains("An error occurred trying to cast String to Number");
  }

  @Test
  @DisplayName("readByYearAndMonth must return status 400 and ProblemDetail when month can not be parsed")
  @Sql(scripts = {"/sql/expense/insert-expense.sql"})
  void readByYearAndMonth_MustReturnsStatus400AndProblemDetail_WhenMonthCanNotBeParsed() {
    HttpEntity<Void> requestEntity = new HttpEntity<>(signIn.generateAuthorizationHeader(EMAIL, PASSWORD));

    ResponseEntity<ProblemDetail> actualResponse = testRestTemplate.exchange(
        URI + "/2o23/2", HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus actualStatusCode = actualResponse.getStatusCode();
    ProblemDetail actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(actualStatusCode).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    Assertions.assertThat(actualResponseBody).isNotNull();
    Assertions.assertThat(actualResponseBody.getTitle()).isEqualTo("Type mismatch");
    Assertions.assertThat(actualResponseBody.getDetail())
        .contains("An error occurred trying to cast String to Number");
  }

  @ParameterizedTest
  @CsvSource(value = {"/1969/3, year must be equals or greater than 1970", "/2100/3, year must be equals or less than 2099"})
  @DisplayName("readByYearAndMonth must return 400 and ProblemDetail when year is out of range")
  @Sql(scripts = {"/sql/expense/insert-expense.sql"})
  void readByYearAndMonth_MustReturn400AndProblemDetail_WhenYearIsOutOfRange(String input, String expectedDetail) {
    HttpEntity<Void> requestEntity = new HttpEntity<>(signIn.generateAuthorizationHeader(EMAIL, PASSWORD));

    ResponseEntity<ProblemDetail> actualResponse = testRestTemplate.exchange(
        URI + input,
        HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus actualStatusCode = actualResponse.getStatusCode();
    ProblemDetail actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(actualStatusCode).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    Assertions.assertThat(actualResponseBody).isNotNull();
    Assertions.assertThat(actualResponseBody.getTitle()).isEqualTo("Invalid arguments");
    Assertions.assertThat(actualResponseBody.getDetail()).isEqualTo(expectedDetail);
  }

  @ParameterizedTest
  @CsvSource(value = {"/2023/13, month must be equals or less than 12", "/2023/0, month must be equals or greater than 1"})
  @DisplayName("readByYearAndMonth must return 400 and ProblemDetail when month is out of range")
  @Sql(scripts = {"/sql/expense/insert-expense.sql"})
  void readByYearAndMonth_MustReturn400AndProblemDetail_WhenMonthIsOutOfRange(String input, String expectedDetail) {
    HttpEntity<Void> requestEntity = new HttpEntity<>(signIn.generateAuthorizationHeader(EMAIL, PASSWORD));

    ResponseEntity<ProblemDetail> actualResponse = testRestTemplate.exchange(
        URI + input,
        HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus actualStatusCode = actualResponse.getStatusCode();
    ProblemDetail actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(actualStatusCode).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    Assertions.assertThat(actualResponseBody).isNotNull();
    Assertions.assertThat(actualResponseBody.getTitle()).isEqualTo("Invalid arguments");
    Assertions.assertThat(actualResponseBody.getDetail()).isEqualTo(expectedDetail);
  }

  @Test
  @DisplayName("readByYearAndMonth must return status 401 when user is not authenticated")
  void readByYearAndMonth_MustReturnStatus401_WhenUserIsNotAuthenticated() {
    ResponseEntity<Void> actualResponse = testRestTemplate.exchange(
        URI + "/2023/1", HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

    HttpStatus actualStatusCode = actualResponse.getStatusCode();

    Assertions.assertThat(actualStatusCode).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  @DisplayName("readByYearAndMonth must return status 404 and ProblemDetail when user has no expenses for march 2023")
  @Sql(scripts = {"/sql/expense/insert-expense.sql"})
  void readByYearAndMonth_MustReturnStatus404AndProblemDetail_WhenUserHasNoExpensesForMarch2023() {
    HttpEntity<Void> requestEntity = new HttpEntity<>(signIn.generateAuthorizationHeader(EMAIL, PASSWORD));

    ResponseEntity<ProblemDetail> actualResponse = testRestTemplate.exchange(
        URI + "/2023/03", HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus actualStatusCode = actualResponse.getStatusCode();
    ProblemDetail actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(actualStatusCode).isEqualByComparingTo(HttpStatus.NOT_FOUND);
    Assertions.assertThat(actualResponseBody).isNotNull();
    Assertions.assertThat(actualResponseBody.getTitle()).isEqualTo("Entity not found");
    Assertions.assertThat(actualResponseBody.getDetail()).isEqualTo("Has no expenses for year 2023 and month MARCH");
  }

}
