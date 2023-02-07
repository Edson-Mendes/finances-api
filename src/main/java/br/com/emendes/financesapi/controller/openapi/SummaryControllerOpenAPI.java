package br.com.emendes.financesapi.controller.openapi;

import br.com.emendes.financesapi.controller.dto.SummaryDto;
import br.com.emendes.financesapi.controller.dto.error.ErrorDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@SecurityRequirement(name = "bearer-key")
@Tag(name = "Resumos")
public interface SummaryControllerOpenAPI {

  @Operation(summary = "Buscar resumo de um mês em determinado ano")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Encontrou resumo"),
      @ApiResponse(responseCode = "400", description = "Algum parâmetro da requisição inválido",
          content = @Content),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "404",
          description = "Resumo não encontrado, usuário não possui receita ou despesas para o dado mês e ano",
          content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))}),
  })
  ResponseEntity<SummaryDto> monthSummary(int year, int month);

}
