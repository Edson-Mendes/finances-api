package br.com.emendes.financesapi.integration.user;

import br.com.emendes.financesapi.dto.problem.ProblemDetail;
import br.com.emendes.financesapi.dto.problem.ValidationProblemDetail;
import br.com.emendes.financesapi.dto.request.ChangePasswordRequest;
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

import static br.com.emendes.financesapi.util.constant.SqlPath.INSERT_USER_SQL_PATH;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("integration")
@DisplayName("Integration tests for PUT /api/users/password")
class ChangePasswordIT {

  @Autowired
  private TestRestTemplate testRestTemplate;
  @Autowired
  private SignIn signIn;

  private final String URI = "/api/users/password";

  private final String USER_EMAIL = "lorem@email.com";
  private final String USER_PASSWORD = "12345678";

  @Test
  @DisplayName("changePassword must return status 204 when change password successfully")
  @Sql(scripts = {INSERT_USER_SQL_PATH})
  void changePassword_MustReturnStatus204_WhenChangePasswordSuccessfully() {
    ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.builder()
        .oldPassword("12345678")
        .newPassword("1234567890")
        .confirm("1234567890")
        .build();

    HttpEntity<ChangePasswordRequest> requestEntity =
        new HttpEntity<>(changePasswordRequest, signIn.generateAuthorizationHeader(USER_EMAIL, USER_PASSWORD));

    ResponseEntity<Void> actualResponse = testRestTemplate
        .exchange(URI, HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus actualStatusCode = actualResponse.getStatusCode();

    Assertions.assertThat(actualStatusCode).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  @DisplayName("changePassword must return status 400 and ProblemDetail when old password is wrong")
  @Sql(scripts = {INSERT_USER_SQL_PATH})
  void changePassword_MustReturnStatus400AndProblemDetail_WhenOldPasswordIsWrong() {
    ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.builder()
        .oldPassword("12345678000")
        .newPassword("1234567890")
        .confirm("1234567890")
        .build();

    HttpEntity<ChangePasswordRequest> requestEntity =
        new HttpEntity<>(changePasswordRequest, signIn.generateAuthorizationHeader(USER_EMAIL, USER_PASSWORD));

    ResponseEntity<ProblemDetail> actualResponse = testRestTemplate
        .exchange(URI, HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus actualStatusCode = actualResponse.getStatusCode();
    ProblemDetail actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(actualStatusCode).isEqualTo(HttpStatus.BAD_REQUEST);
    Assertions.assertThat(actualResponseBody).isNotNull();
    Assertions.assertThat(actualResponseBody.getTitle()).isEqualTo("Wrong password");
    Assertions.assertThat(actualResponseBody.getDetail()).isEqualTo("Wrong password");
  }

  @Test
  @DisplayName("changePassword must return status 400 and ProblemDetail when password and confirm do not match")
  @Sql(scripts = {INSERT_USER_SQL_PATH})
  void changePassword_MustReturnStatus400AndProblemDetail_WhenPasswordAndConfirmDoNotMatch() {
    ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.builder()
        .oldPassword("12345678")
        .newPassword("1234567890")
        .confirm("12345678900")
        .build();

    HttpEntity<ChangePasswordRequest> requestEntity =
        new HttpEntity<>(changePasswordRequest, signIn.generateAuthorizationHeader(USER_EMAIL, USER_PASSWORD));

    ResponseEntity<ProblemDetail> actualResponse = testRestTemplate
        .exchange(URI, HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus actualStatusCode = actualResponse.getStatusCode();
    ProblemDetail actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(actualStatusCode).isEqualTo(HttpStatus.BAD_REQUEST);
    Assertions.assertThat(actualResponseBody).isNotNull();
    Assertions.assertThat(actualResponseBody.getTitle()).isEqualTo("Passwords do not match");
    Assertions.assertThat(actualResponseBody.getDetail()).isEqualTo("Passwords do not match");
  }

  @Test
  @DisplayName("changePassword must returns status 400 and ValidationProblemDetail when request body is invalid")
  @Sql(scripts = {INSERT_USER_SQL_PATH})
  void changePassword_MustReturnStatus400AndValidationProblemDetail_WhenRequestBodyIsInvalid() {
    ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.builder()
        .oldPassword("")
        .newPassword("   ")
        .confirm(null)
        .build();

    HttpEntity<ChangePasswordRequest> requestEntity =
        new HttpEntity<>(changePasswordRequest, signIn.generateAuthorizationHeader(USER_EMAIL, USER_PASSWORD));

    ResponseEntity<ValidationProblemDetail> actualResponse = testRestTemplate
        .exchange(URI, HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus actualStatusCode = actualResponse.getStatusCode();
    ValidationProblemDetail actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(actualStatusCode).isEqualTo(HttpStatus.BAD_REQUEST);
    Assertions.assertThat(actualResponseBody).isNotNull();
    Assertions.assertThat(actualResponseBody.getFields()).contains("oldPassword", "newPassword", "confirm");
    Assertions.assertThat(actualResponseBody.getMessages())
        .contains("oldPassword must not be null or blank",
            "newPassword must not be null or blank", "confirm must not be null or blank");
  }

  @Test
  @DisplayName("changePassword must return status 401 when user is not authenticated")
  void changePassword_MustReturnStatus401_WhenUserIsNotAuthenticated() {
    ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.builder()
        .oldPassword("12345678")
        .newPassword("1234567890")
        .confirm("1234567890")
        .build();

    HttpEntity<ChangePasswordRequest> requestEntity = new HttpEntity<>(changePasswordRequest);

    ResponseEntity<Void> actualResponse = testRestTemplate
        .exchange(URI, HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus actualStatusCode = actualResponse.getStatusCode();

    Assertions.assertThat(actualStatusCode).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

}
