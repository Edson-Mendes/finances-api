package br.com.emendes.financesapi.integration.category;

import br.com.emendes.financesapi.dto.response.CategoryResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("integration")
@DisplayName("Tests for GET /api/categories")
class FetchAllCategoriesIT {

  @Autowired
  private TestRestTemplate testRestTemplate;

  private final String URI = "/api/categories";

  @Test
  @DisplayName("fetchAllCategories must return 200 and List<CategoryResponse> when fetch successfully")
  void fetchAllCategories_MustReturn200AndListCategoryResponse_WhenFetchSuccessfully() {
    ResponseEntity<List<CategoryResponse>> actualResponse = testRestTemplate
        .exchange(URI, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

    HttpStatus actualStatusCode = actualResponse.getStatusCode();
    List<CategoryResponse> actualResponseBody = actualResponse.getBody();

    Assertions.assertThat(actualStatusCode).isEqualByComparingTo(HttpStatus.OK);
    Assertions.assertThat(actualResponseBody).isNotEmpty().hasSize(8);
  }

}
