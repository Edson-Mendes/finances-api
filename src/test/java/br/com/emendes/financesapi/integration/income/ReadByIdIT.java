package br.com.emendes.financesapi.integration.income;

import br.com.emendes.financesapi.dto.problem.ProblemDetail;
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

import static br.com.emendes.financesapi.util.constant.SqlPath.INSERT_INCOME_SQL_PATH;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("integration")
@DisplayName("Integration tests for GET /api/incomes/{incomeId}")
class ReadByIdIT {

  @Autowired
  private TestRestTemplate testRestTemplate;
  @Autowired
  private SignIn signIn;

  private final String URI = "/api/incomes";
  private final String EMAIL = "lorem@email.com";
  private final String PASSWORD = "12345678";

  @Test
  @DisplayName("readById must return status 200 and IncomeResponse when found successfully")
  @Sql(scripts = {INSERT_INCOME_SQL_PATH})
  void readById_MustReturnStatus200AndIncomeResponse_WhenFoundSuccessfully() {
    HttpEntity<Void> requestEntity = new HttpEntity<>(signIn.generateAuthorizationHeader(EMAIL, PASSWORD));

    ResponseEntity<IncomeResponse> actualResponse = testRestTemplate.exchange(
        URI + "/1", HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus actualStatusCode = actualResponse.getStatusCode();
    IncomeResponse actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(actualStatusCode).isEqualByComparingTo(HttpStatus.OK);
    Assertions.assertThat(actualResponseBody).isNotNull();
    Assertions.assertThat(actualResponseBody.getId()).isEqualTo(1L);
    Assertions.assertThat(actualResponseBody.getDescription()).isEqualTo("Salário");
  }

  @Test
  @DisplayName("readById must return status 400 and ProblemDetail when id is invalid")
  @Sql(scripts = {INSERT_INCOME_SQL_PATH})
  void readById_MustReturnStatus400AndProblemDetail_WhenIdIsInvalid() {
    HttpEntity<Void> requestEntity = new HttpEntity<>(signIn.generateAuthorizationHeader(EMAIL, PASSWORD));

    ResponseEntity<ProblemDetail> actualResponse = testRestTemplate.exchange(
        URI + "/1oo", HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus actualStatusCode = actualResponse.getStatusCode();
    ProblemDetail actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(actualStatusCode).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    Assertions.assertThat(actualResponseBody).isNotNull();
    Assertions.assertThat(actualResponseBody.getTitle()).isEqualTo("Type mismatch");
    Assertions.assertThat(actualResponseBody.getDetail())
        .isEqualTo("An error occurred trying to cast String to Number");
  }

  @Test
  @DisplayName("readById must return status 401 when user is not authenticated")
  void readById_MustReturnStatus401_WhenUserIsNotAuthenticated() {
    ResponseEntity<Void> actualResponse = testRestTemplate.exchange(
        URI + "/1", HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

    HttpStatus actualStatusCode = actualResponse.getStatusCode();

    Assertions.assertThat(actualStatusCode).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  @DisplayName("readById must return status 404 and ProblemDetail when id not exists")
  @Sql(scripts = {INSERT_INCOME_SQL_PATH})
  void readById_MustReturnStatus404AndProblemDetail_WhenIdNotExists() {
    HttpEntity<Void> requestEntity = new HttpEntity<>(signIn.generateAuthorizationHeader(EMAIL, PASSWORD));

    ResponseEntity<ProblemDetail> actualResponse = testRestTemplate.exchange(
        URI + "/10000", HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus actualStatusCode = actualResponse.getStatusCode();
    ProblemDetail actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(actualStatusCode).isEqualByComparingTo(HttpStatus.NOT_FOUND);
    Assertions.assertThat(actualResponseBody).isNotNull();
    Assertions.assertThat(actualResponseBody.getTitle()).isEqualTo("Entity not found");
    Assertions.assertThat(actualResponseBody.getDetail())
        .isEqualTo("Income not found");
  }

}
