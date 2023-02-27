package br.com.emendes.financesapi.controller.openapi;

import br.com.emendes.financesapi.dto.problem.ProblemDetail;
import br.com.emendes.financesapi.dto.problem.ValidationProblemDetail;
import br.com.emendes.financesapi.dto.response.ExpenseResponse;
import br.com.emendes.financesapi.dto.request.ExpenseRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@SecurityRequirement(name = "bearer-key")
@Tag(name = "Despesas")
public interface ExpenseControllerOpenAPI {

  @Operation(summary = "Salvar uma despesa")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Despesa salva", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ExpenseResponse.class))}),
      @ApiResponse(responseCode = "400", description = "Bad request - Algum parâmetro do corpo da requisição inválido",
          content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ValidationProblemDetail.class))}),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "409", description = "Já existe despesa com essa descrição no mesmo mês e ano",
          content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))}),
  })
  ResponseEntity<ExpenseResponse> create(ExpenseRequest form, UriComponentsBuilder uriBuilder);

  @Operation(summary = "Buscar todas as despesas do usuário, opcional buscar por descrição")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Encontrou despesas"),
      @ApiResponse(responseCode = "400", description = "Bad request - Algum parâmetro da requisição inválido",
          content = @Content),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "404", description = "Nenhuma despesa encontrada",
          content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))}),
  })
  ResponseEntity<Page<ExpenseResponse>> read(String description, @ParameterObject Pageable pageable);

  @Operation(summary = "Buscar despesa por id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Encontrou a despesa"),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "404", description = "Despesa não encontrada",
          content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))}),
  })
  ResponseEntity<ExpenseResponse> readById(Long id);

  @Operation(summary = "Buscar despesas do usuário por ano e mês")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Encontrou despesas"),
      @ApiResponse(responseCode = "400", description = "Bad request - Algum parâmetro da requisição inválido",
          content = @Content),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "404", description = "Nenhuma despesa encontrada",
          content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))}),
  })
  ResponseEntity<Page<ExpenseResponse>> readByYearAndMonth(
      @Min(1970) @Max(2099) int year, // TODO: Adicionar essa validação na service.
      @Min(1) @Max(12) int month,
      @ParameterObject Pageable pageable);

  @Operation(summary = "Atualizar despesa por id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Atualizado com sucesso"),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "400", description = "Bad request - Algum parâmetro do corpo da requisição inválido",
          content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ValidationProblemDetail.class))}),
      @ApiResponse(responseCode = "404", description = "Despesa não encontrada",
          content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))}),
      @ApiResponse(responseCode = "409", description = "Já existe despesa com essa descrição no mesmo mês e ano",
          content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))}),
  })
  ResponseEntity<ExpenseResponse> update(@PathVariable Long id, @Valid @RequestBody ExpenseRequest expenseRequest);

  @Operation(summary = "Deletar despesa por id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Deletado com sucesso"),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "404", description = "Despesa não encontrada",
          content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))}),
  })
  ResponseEntity<Void> delete(@PathVariable Long id);

}
