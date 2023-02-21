package br.com.emendes.financesapi.service.impl;

import br.com.emendes.financesapi.dto.response.CategoryResponse;
import br.com.emendes.financesapi.model.Category;
import br.com.emendes.financesapi.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

  @Override
  public List<CategoryResponse> fetchAllCategories() {
    return Arrays.stream(Category.values()).map(c -> new CategoryResponse(c.name())).collect(Collectors.toList());
  }

}
