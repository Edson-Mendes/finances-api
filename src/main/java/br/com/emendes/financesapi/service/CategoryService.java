package br.com.emendes.financesapi.service;

import br.com.emendes.financesapi.controller.dto.CategoryResponse;

import java.util.List;

public interface CategoryService {

  List<CategoryResponse> fetchAllCategories();

}
