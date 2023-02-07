package br.com.emendes.financesapi.controller.openapi;

import br.com.emendes.financesapi.controller.dto.IncomeDto;
import br.com.emendes.financesapi.controller.dto.error.ErrorDto;
import br.com.emendes.financesapi.controller.dto.error.FormErrorDto;
import br.com.emendes.financesapi.controller.form.IncomeForm;
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

@SecurityRequirement(name = "bearer-key")
@Tag(name = "Receitas")
public interface IncomeControllerOpenAPI {

  @Operation(summary = "Salvar uma receita")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Receita salva",
          content = {@Content(mediaType = "application/json", schema = @Schema(implementation = IncomeDto.class))}),
      @ApiResponse(responseCode = "400", description = "Bad request - Algum parâmetro do corpo da requisição inválido",
          content = {@Content(mediaType = "application/json", schema = @Schema(implementation = FormErrorDto.class))}),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "409", description = "Já existe receita com essa descrição no mesmo mês e ano",
          content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))}),
  })
  ResponseEntity<IncomeDto> create(@Valid @RequestBody IncomeForm form, UriComponentsBuilder uriBuilder);

  @Operation(summary = "Buscar todas as receitas do usuário, opcional buscar por descrição")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Encontrou receitas"),
      @ApiResponse(responseCode = "400", description = "Bad request - Algum parâmetro da requisição inválido",
          content = @Content),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "404", description = "Nenhuma receita encontrada",
          content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))}),
  })
  ResponseEntity<Page<IncomeDto>> read(String description, @ParameterObject() Pageable pageable);

  @Operation(summary = "Buscar receita por id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Encontrou a receita"),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "404", description = "Receita não encontrada",
          content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))}),
  })
  ResponseEntity<IncomeDto> readById(Long id);

  @Operation(summary = "Buscar receitas do usuário por ano e mês")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Encontrou receitas"),
      @ApiResponse(responseCode = "400", description = "Bad request - Algum parâmetro da requisição inválido",
          content = @Content),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "404", description = "Nenhuma receita encontrada",
          content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))}),
  })
  ResponseEntity<Page<IncomeDto>> readByYearAndMonth(int year, int month, @ParameterObject Pageable pageable);

  @Operation(summary = "Atualizar receita por id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Atualizado com sucesso"),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "400", description = "Bad request - Algum parâmetro do corpo da requisição inválido", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = FormErrorDto.class))}),
      @ApiResponse(responseCode = "404", description = "Receita não encontrada", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))}),
      @ApiResponse(responseCode = "409", description = "Já existe receita com essa descrição no mesmo mês e ano", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))}),
  })
  ResponseEntity<IncomeDto> update(Long id, IncomeForm incomeForm);

  @Operation(summary = "Deletar receita por id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Deletado com sucesso"),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "404", description = "Receita não encontrada", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))}),
  })
  ResponseEntity<Void> delete(@PathVariable Long id);
}
