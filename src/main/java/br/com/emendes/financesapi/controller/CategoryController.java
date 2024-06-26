package br.com.emendes.financesapi.controller;

import br.com.emendes.financesapi.controller.openapi.CategoryControllerOpenAPI;
import br.com.emendes.financesapi.dto.response.CategoryResponse;
import br.com.emendes.financesapi.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Classe controller para lidar com os endpoints /api/categories/**.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/categories", produces = "application/json;charset=UTF-8")
public class CategoryController implements CategoryControllerOpenAPI {

  private final CategoryService categoryService;

  /**
   * Método responsável por GET /api/categories.
   */
  @Override
  @GetMapping
  public ResponseEntity<List<CategoryResponse>> fetchAllCategories() {
    return ResponseEntity.ok(categoryService.fetchAllCategories());
  }

}
