package br.com.emendes.financesapi.unit.controller;

import br.com.emendes.financesapi.controller.CategoryController;
import br.com.emendes.financesapi.dto.response.CategoryResponse;
import br.com.emendes.financesapi.model.Category;
import br.com.emendes.financesapi.service.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {CategoryController.class}),
    excludeAutoConfiguration = SecurityAutoConfiguration.class,
    useDefaultFilters = false
)
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
      List<CategoryResponse> categoryResponseList = Arrays.stream(Category.values())
          .map(c -> new CategoryResponse(c.name())).collect(Collectors.toList());

      BDDMockito.when(categoryServiceMock.fetchAllCategories()).thenReturn(categoryResponseList);
      mockMvc.perform(get(CATEGORY_BASE_URI))
          .andExpect(status().isOk());
    }

  }

}