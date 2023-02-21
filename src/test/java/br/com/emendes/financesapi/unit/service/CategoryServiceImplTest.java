package br.com.emendes.financesapi.unit.service;

import br.com.emendes.financesapi.dto.response.CategoryResponse;
import br.com.emendes.financesapi.service.impl.CategoryServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class)
@DisplayName("Unit tests for CategoryServiceImpl")
class CategoryServiceImplTest {

  @InjectMocks
  private CategoryServiceImpl categoryService;

  @Nested
  @DisplayName("Tests for fetchAllCategories method")
  class FetchAllCategoriesMethod {

    @Test
    @DisplayName("fetchAllCategories must return List<CategoryResponse> when fetch successfully")
    void fetchAllCategories_MustReturnListCategoryResponse_WhenFetchSuccessfully() {

      List<CategoryResponse> actualCategoryResponseList = categoryService.fetchAllCategories();

      Assertions.assertThat(actualCategoryResponseList).isNotNull().isNotEmpty().hasSize(8);
    }

  }

}