package br.com.emendes.financesapi.integration.income;

import br.com.emendes.financesapi.dto.problem.ProblemDetail;
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

import static br.com.emendes.financesapi.util.constant.AuthenticationConstant.USER_EMAIL;
import static br.com.emendes.financesapi.util.constant.AuthenticationConstant.USER_PASSWORD;
import static br.com.emendes.financesapi.util.constant.SqlPath.INSERT_INCOME_SQL_PATH;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("integration")
@DisplayName("Integration tests for DELETE /api/incomes/{incomeId}")
class DeleteIT {

  @Autowired
  private TestRestTemplate testRestTemplate;
  @Autowired
  private SignIn signIn;

  private final String URI = "/api/incomes";

  @Test
  @DisplayName("delete must return status 204 when delete successfully")
  @Sql(scripts = {INSERT_INCOME_SQL_PATH})
  void delete_MustReturnStatus204_WhenDeletedSuccessful() {
    HttpEntity<Void> requestEntity = new HttpEntity<>(signIn.generateAuthorizationHeader(USER_EMAIL, USER_PASSWORD));

    ResponseEntity<Void> actualResponse = testRestTemplate.exchange(
        URI + "/1", HttpMethod.DELETE, requestEntity, new ParameterizedTypeReference<>() {
        });

    HttpStatusCode actualStatusCode = actualResponse.getStatusCode();

    Assertions.assertThat(actualStatusCode).isEqualTo(HttpStatusCode.valueOf(204));
  }

  @Test
  @DisplayName("delete must return status 400 and ProblemDetail when id is invalid")
  @Sql(scripts = {INSERT_INCOME_SQL_PATH})
  void delete_MustReturnStatus400AndProblemDetail_WhenIdIsInvalid() {
    HttpEntity<Void> requestEntity = new HttpEntity<>(signIn.generateAuthorizationHeader(USER_EMAIL, USER_PASSWORD));

    ResponseEntity<ProblemDetail> actualResponse = testRestTemplate.exchange(
        URI + "/1o0", HttpMethod.DELETE, requestEntity, new ParameterizedTypeReference<>() {
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
  @DisplayName("delete must return status 401 when user is not authenticated")
  void delete_MustReturnStatus401_WhenUserIsNotAuthenticated() {
    ResponseEntity<Void> actualResponse = testRestTemplate.exchange(
        URI + "/1", HttpMethod.DELETE, null, new ParameterizedTypeReference<>() {
        });

    HttpStatusCode actualStatusCode = actualResponse.getStatusCode();

    Assertions.assertThat(actualStatusCode).isEqualTo(HttpStatusCode.valueOf(401));
  }

  @Test
  @DisplayName("delete must return status 404 and ProblemDetail when id no exists")
  @Sql(scripts = {INSERT_INCOME_SQL_PATH})
  void delete_MustReturnStatus404AndProblemDetail_WhenIdNoExists() {
    HttpEntity<Void> requestEntity = new HttpEntity<>(signIn.generateAuthorizationHeader(USER_EMAIL, USER_PASSWORD));
    ResponseEntity<ProblemDetail> actualResponse = testRestTemplate.exchange(
        URI + "/10000", HttpMethod.DELETE, requestEntity, new ParameterizedTypeReference<>() {
        });

    HttpStatusCode actualStatusCode = actualResponse.getStatusCode();
    ProblemDetail actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(actualStatusCode).isEqualTo(HttpStatusCode.valueOf(404));
    Assertions.assertThat(actualResponseBody).isNotNull();
    Assertions.assertThat(actualResponseBody.getTitle()).isEqualTo("Entity not found");
    Assertions.assertThat(actualResponseBody.getDetail()).isEqualTo("Income not found with id: 10000");
  }

}
