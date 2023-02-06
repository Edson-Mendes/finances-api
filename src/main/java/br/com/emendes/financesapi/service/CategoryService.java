package br.com.emendes.financesapi.service;

import br.com.emendes.financesapi.controller.dto.CategoryResponse;
import br.com.emendes.financesapi.model.Category;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

// TODO: Criar testes automatizados.
@Service
public class CategoryService {

  public List<CategoryResponse> fetchAllCategories() {
    return Arrays.stream(Category.values()).map(c -> new CategoryResponse(c.name())).toList();
  }

}
