package br.com.emendes.financesapi.controller.openapi;

import br.com.emendes.financesapi.dto.response.CategoryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@SecurityRequirement(name = "bearer-key")
@Tag(name = "Categorias")
public interface CategoryControllerOpenAPI {

  @Operation(summary = "Buscar todas as categorias")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Busca todas as categorias.", content = @Content),
  })
  ResponseEntity<List<CategoryResponse>> fetchAllCategories();

}
