package br.com.emendes.financesapi.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.emendes.financesapi.config.security.TokenService;
import br.com.emendes.financesapi.config.validation.error_dto.ErrorDto;
import br.com.emendes.financesapi.controller.dto.SummaryDto;
import br.com.emendes.financesapi.service.SummaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/resumo")
public class SummaryController {

  @Autowired
  private SummaryService summaryService;

  @Autowired
  private TokenService tokenService;

  @Operation(summary = "Buscar resumo de um mês em determinado ano", security = {
      @SecurityRequirement(name = "bearer-key") })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Encontrou resumo"),
      @ApiResponse(responseCode = "400", description = "Algum parâmetro da requisição inválido", content = @Content),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "404", description = "Resumo não encontrado, usuário não possui receita ou despesas para o dado mês e ano", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class)) }),
  })
  @GetMapping("/{year}/{month}")
  public ResponseEntity<SummaryDto> monthSummary(@PathVariable Integer year, @PathVariable Integer month,
      HttpServletRequest request) {
    Long userId = tokenService.getUserId(request);
    return summaryService.monthSummary(year, month, userId);
  }

}
