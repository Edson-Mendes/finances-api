package br.com.emendes.financesapi.service.impl;

import br.com.emendes.financesapi.controller.dto.CategoryResponse;
import br.com.emendes.financesapi.model.Category;
import br.com.emendes.financesapi.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

// TODO: Criar testes automatizados.
@Service
public class CategoryServiceImpl implements CategoryService {

  @Override
  public List<CategoryResponse> fetchAllCategories() {
    return Arrays.stream(Category.values()).map(c -> new CategoryResponse(c.name())).toList();
  }

}
