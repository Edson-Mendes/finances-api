package br.com.emendes.financesapi.unit.controller;

import br.com.emendes.financesapi.controller.CategoryController;
import br.com.emendes.financesapi.service.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(value = CategoryController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@DisplayName("Tests for CategoryController")
class CategoryControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private CategoryService categoryServiceMock;

  private static final String CATEGORY_BASE_URI = "/api/categories";

  @Nested
  @DisplayName("Tests for fetchAllCategories endpoint")
  class FetchAllCategoriesEndpoint {

    @Test
    @DisplayName("fetchAllCategories must return List<CategoryResponse> when fetch successfully")
    void fetchAllCategories_MustReturnListCategoryResponse_WhenFetchSuccessfully() throws Exception {
      mockMvc.perform(get(CATEGORY_BASE_URI))
          .andExpect(status().isOk());
    }

  }

}