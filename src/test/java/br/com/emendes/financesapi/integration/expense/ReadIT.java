package br.com.emendes.financesapi.integration.expense;

import br.com.emendes.financesapi.dto.problem.ProblemDetail;
import br.com.emendes.financesapi.dto.response.ExpenseResponse;
import br.com.emendes.financesapi.util.component.SignIn;
import br.com.emendes.financesapi.util.wrapper.PageableResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

import static br.com.emendes.financesapi.util.constant.SqlPath.INSERT_EXPENSE_SQL_PATH;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("integration")
@DisplayName("Integration tests for GET /api/expenses")
class ReadIT {

  @Autowired
  private TestRestTemplate testRestTemplate;
  @Autowired
  private SignIn signIn;

  private final String URI = "/api/expenses";
  private final String EMAIL = "lorem@email.com";
  private final String PASSWORD = "12345678";

  @Test
  @DisplayName("read must return status 200 and Page<ExpenseResponse> when read successfully")
  @Sql(scripts = {INSERT_EXPENSE_SQL_PATH})
  void read_MustReturnStatus200AndPageExpenseResponse_WhenReadSuccessfully() {
    HttpEntity<Void> requestEntity = new HttpEntity<>(signIn.generateAuthorizationHeader(EMAIL, PASSWORD));

    ResponseEntity<PageableResponse<ExpenseResponse>> actualResponse = testRestTemplate
        .exchange(URI, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus actualStatusCode = actualResponse.getStatusCode();
    Page<ExpenseResponse> actualResponseBody = actualResponse.getBody();
    Assertions.assertThat(actualResponseBody).isNotNull().isNotEmpty().hasSize(2);

    List<String> actualDescriptionList = actualResponseBody.stream().map(ExpenseResponse::getDescription).toList();

    Assertions.assertThat(actualStatusCode).isEqualByComparingTo(HttpStatus.OK);
    Assertions.assertThat(actualDescriptionList).contains("Aluguel", "Supermercado");
  }

  @Test
  @DisplayName("read must return status 200 and empty Page<ExpenseResponse> when read page one")
  @Sql(scripts = {INSERT_EXPENSE_SQL_PATH})
  void read_MustReturnStatus200AndEmptyPageExpenseResponse_WhenReadPageOne() {
    HttpEntity<Void> requestEntity = new HttpEntity<>(signIn.generateAuthorizationHeader(EMAIL, PASSWORD));

    ResponseEntity<PageableResponse<ExpenseResponse>> actualResponse = testRestTemplate
        .exchange(URI + "?page=1", HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus actualStatusCode = actualResponse.getStatusCode();
    Page<ExpenseResponse> actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(actualStatusCode).isEqualByComparingTo(HttpStatus.OK);
    Assertions.assertThat(actualResponseBody).isNotNull().isEmpty();
    Assertions.assertThat(actualResponseBody.getTotalPages()).isEqualTo(1);
    Assertions.assertThat(actualResponseBody.getTotalElements()).isEqualTo(2L);
  }

  @Test
  @DisplayName("read must returns status 401 when user is not authenticated")
  void read_MustReturnsStatus401_WhenUserIsNotAuthenticated() {
    ResponseEntity<Void> actualResponse = testRestTemplate.exchange(
        URI, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

    Assertions.assertThat(actualResponse.getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  @DisplayName("read must return status 404 and ProblemDetail when user has no expenses")
  @Sql(scripts = {"/sql/user/insert-user.sql"})
  void read_MustReturnStatus404AndProblemDetail_WhenUserHasNoExpenses() {
    HttpEntity<Void> requestEntity = new HttpEntity<>(signIn.generateAuthorizationHeader(EMAIL, PASSWORD));

    ResponseEntity<ProblemDetail> actualResponse = testRestTemplate.exchange(
        URI, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus actualStatusCode = actualResponse.getStatusCode();
    ProblemDetail actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(actualStatusCode).isEqualByComparingTo(HttpStatus.NOT_FOUND);
    Assertions.assertThat(actualResponseBody).isNotNull();
    Assertions.assertThat(actualResponseBody.getTitle()).isEqualTo("Entity not found");
    Assertions.assertThat(actualResponseBody.getDetail()).isEqualTo("The user has no expenses");
  }

  @Test
  @DisplayName("readByDescription must return status 200 and empty Page<ExpenseResponse> when read by description and page one")
  @Sql(scripts = {INSERT_EXPENSE_SQL_PATH})
  void readByDescription_MustReturnsStatus200AndEmptyPageExpenseResponse_WhenReadByDescriptionAndPageOne() {
    HttpEntity<Void> requestEntity = new HttpEntity<>(signIn.generateAuthorizationHeader(EMAIL, PASSWORD));

    ResponseEntity<PageableResponse<ExpenseResponse>> actualResponse = testRestTemplate
        .exchange(URI + "?description=aluguel&page=1",
            HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus actualStatusCode = actualResponse.getStatusCode();
    Page<ExpenseResponse> actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(actualStatusCode).isEqualByComparingTo(HttpStatus.OK);
    Assertions.assertThat(actualResponseBody).isNotNull().isEmpty();
    Assertions.assertThat(actualResponseBody.getTotalPages()).isEqualTo(1);
    Assertions.assertThat(actualResponseBody.getTotalElements()).isEqualTo(1L);
  }

  @Test
  @DisplayName("readByDescription must returns status 200 and Page<ExpenseResponse> when read by description successfully")
  @Sql(scripts = {INSERT_EXPENSE_SQL_PATH})
  void readByDescription_MustReturnStatus200AndPageExpenseResponse_WhenReadByDescriptionSuccessfully() {
    HttpEntity<Void> requestEntity = new HttpEntity<>(signIn.generateAuthorizationHeader(EMAIL, PASSWORD));

    ResponseEntity<PageableResponse<ExpenseResponse>> actualResponse = testRestTemplate
        .exchange(URI + "?description=alu", HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus actualStatusCode = actualResponse.getStatusCode();
    Page<ExpenseResponse> actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(actualStatusCode).isEqualByComparingTo(HttpStatus.OK);
    Assertions.assertThat(actualResponseBody).isNotNull().isNotEmpty().hasSize(1);
    Assertions.assertThat(actualResponseBody.getContent().get(0).getDescription()).isEqualTo("Aluguel");
  }

  @Test
  @DisplayName("readByDescription must returns status 404 and ProblemDetail when user has no expenses with description \"Aluguel\"")
  @Sql(scripts = {"/sql/user/insert-user.sql"})
  void readByDescription_MustReturnStatus404AndProblemDetail_WhenUserHasNoExpensesWithDescriptionAluguel() {
    HttpEntity<Void> requestEntity = new HttpEntity<>(signIn.generateAuthorizationHeader(EMAIL, PASSWORD));
    ResponseEntity<ProblemDetail> actualResponse = testRestTemplate.exchange(
        URI + "?description=Aluguel", HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus actualStatusCode = actualResponse.getStatusCode();
    ProblemDetail actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(actualStatusCode).isEqualByComparingTo(HttpStatus.NOT_FOUND);
    Assertions.assertThat(actualResponseBody).isNotNull();
    Assertions.assertThat(actualResponseBody.getTitle()).isEqualTo("Entity not found");
    Assertions.assertThat(actualResponseBody.getDetail())
        .contains("The user has no expenses with a description similar to Aluguel");
  }

}
