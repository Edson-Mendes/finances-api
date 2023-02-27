package br.com.emendes.financesapi.integration.user;

import br.com.emendes.financesapi.dto.response.UserResponse;
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

import static br.com.emendes.financesapi.util.constant.SqlPath.INSERT_ADMIN_SQL_PATH;
import static br.com.emendes.financesapi.util.constant.SqlPath.INSERT_USER_SQL_PATH;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("integration")
@DisplayName("Integration tests for GET /api/users")
class ReadAllIT {

  @Autowired
  private TestRestTemplate testRestTemplate;
  @Autowired
  private SignIn signIn;

  private final String URI = "/api/users";

  private final String USER_EMAIL = "lorem@email.com";
  private final String USER_PASSWORD = "12345678";
  private final String ADMIN_EMAIL = "admin@email.com";
  private final String ADMIN_PASSWORD = "12345678";


  @Test
  @DisplayName("readAll must return Page<UserResponse> when user is admin")
  @Sql(scripts = {INSERT_ADMIN_SQL_PATH, INSERT_USER_SQL_PATH})
  void readAll_MustReturnPageUserResponse_WhenUserIsAdmin() {
    HttpEntity<Void> requestEntity = new HttpEntity<>(signIn.generateAuthorizationHeader(ADMIN_EMAIL, ADMIN_PASSWORD));
    ResponseEntity<PageableResponse<UserResponse>> actualResponse = testRestTemplate
        .exchange(URI, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus actualStatusCode = actualResponse.getStatusCode();
    Page<UserResponse> actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(actualStatusCode).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(actualResponseBody).isNotNull().isNotEmpty().hasSize(2);
    Assertions.assertThat(actualResponseBody.getContent().get(0).getName()).isEqualTo("Admin");
    Assertions.assertThat(actualResponseBody.getContent().get(0).getEmail()).isEqualTo("admin@email.com");
    Assertions.assertThat(actualResponseBody.getContent().get(1).getName()).isEqualTo("Lorem Ipsum");
    Assertions.assertThat(actualResponseBody.getContent().get(1).getEmail()).isEqualTo("lorem@email.com");
  }

  @Test
  @DisplayName("readAll must return status 401 when user is not authenticated")
  void readAll_MustReturnStatus401_WhenUserIsNotAuthenticated() {
    ResponseEntity<Void> actualResponse = testRestTemplate
        .exchange(URI, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

    HttpStatus actualStatusCode = actualResponse.getStatusCode();

    Assertions.assertThat(actualStatusCode).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  @DisplayName("readAll must return status 403 when user is not admin")
  @Sql(scripts = {INSERT_USER_SQL_PATH})
  void readAll_MustReturnStatus403_WhenUSerIsNotAdmin() {
    HttpEntity<Void> requestEntity = new HttpEntity<>(signIn.generateAuthorizationHeader(USER_EMAIL, USER_PASSWORD));
    ResponseEntity<Void> actualResponse = testRestTemplate
        .exchange(URI, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus actualStatusCode = actualResponse.getStatusCode();

    Assertions.assertThat(actualStatusCode).isEqualTo(HttpStatus.FORBIDDEN);
  }

}
