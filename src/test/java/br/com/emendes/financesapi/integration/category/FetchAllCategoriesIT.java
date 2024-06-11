package br.com.emendes.financesapi.integration.category;

import br.com.emendes.financesapi.dto.response.CategoryResponse;
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

import java.util.List;

import static br.com.emendes.financesapi.util.constant.SqlPath.INSERT_USER_SQL_PATH;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("integration")
@DisplayName("Tests for GET /api/categories")
class FetchAllCategoriesIT {

  @Autowired
  private TestRestTemplate testRestTemplate;

  @Autowired
  private SignIn signIn;

  private final String EMAIL = "lorem@email.com";
  private final String PASSWORD = "12345678";
  private final String URI = "/api/categories";

  @Test
  @DisplayName("fetchAllCategories must return 200 and List<CategoryResponse> when fetch successfully")
  @Sql(scripts = INSERT_USER_SQL_PATH)
  void fetchAllCategories_MustReturn200AndListCategoryResponse_WhenFetchSuccessfully() {
    HttpEntity<Void> requestEntity = new HttpEntity<>(signIn.generateAuthorizationHeader(EMAIL, PASSWORD));
    ResponseEntity<List<CategoryResponse>> actualResponse = testRestTemplate
        .exchange(URI, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {
        });

    HttpStatusCode actualStatusCode = actualResponse.getStatusCode();
    List<CategoryResponse> actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(actualStatusCode).isEqualTo(HttpStatusCode.valueOf(200));
    Assertions.assertThat(actualResponseBody).isNotEmpty().hasSize(8);
  }

  @Test
  @DisplayName("fetchAllCategories must returns status 401 when user is not authenticated")
  void fetchAllCategories_MustReturnsStatus401_WhenUserIsNotAuthenticated() {
    ResponseEntity<Void> actualResponse = testRestTemplate.exchange(
        URI, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
        });

    Assertions.assertThat(actualResponse.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(401));
  }

}
