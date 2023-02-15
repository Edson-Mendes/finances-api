package br.com.emendes.financesapi.service;

import br.com.emendes.financesapi.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {

  List<CategoryResponse> fetchAllCategories();

}
