package br.com.emendes.financesapi.controller;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.emendes.financesapi.controller.dto.IncomeDto;
import br.com.emendes.financesapi.controller.dto.error.ErrorDto;
import br.com.emendes.financesapi.controller.dto.error.FormErrorDto;
import br.com.emendes.financesapi.controller.form.IncomeForm;
import br.com.emendes.financesapi.service.IncomeService;
import br.com.emendes.financesapi.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/receitas")
public class IncomeController {

  @Autowired
  private IncomeService incomeService;

  @Autowired
  TokenService tokenService;

  @Operation(summary = "Salvar uma receita", security = { @SecurityRequirement(name = "bearer-key") })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Receita salva", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = IncomeDto.class)) }),
      @ApiResponse(responseCode = "400", description = "Bad request - Algum parâmetro do corpo da requisição inválido", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = FormErrorDto.class)) }),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "409", description = "Já existe receita com essa descrição no mesmo mês e ano", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class)) }),
  })
  @PostMapping
  public ResponseEntity<IncomeDto> create(@Valid @RequestBody IncomeForm form, UriComponentsBuilder uriBuilder,
      HttpServletRequest request) {
    Long userId = tokenService.getUserId(request);
    IncomeDto incomeDto = incomeService.create(form, userId);
    URI uri = uriBuilder.path("/despesas/{id}").buildAndExpand(incomeDto.getId()).toUri();
    return ResponseEntity.created(uri).body(incomeDto);
  }

  @Operation(summary = "Buscar todas as receitas do usuário, opcional buscar por descrição", security = {
      @SecurityRequirement(name = "bearer-key") })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Encontrou receitas"),
      @ApiResponse(responseCode = "400", description = "Bad request - Algum parâmetro da requisição inválido", content = @Content),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "404", description = "Nenhuma receita encontrada", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class)) }),
  })
  @GetMapping
  public ResponseEntity<Page<IncomeDto>> read(
      @RequestParam(required = false) String description,
      @ParameterObject() @PageableDefault(sort = "date", direction = Direction.DESC, page = 0, size = 10) Pageable pageable,
      HttpServletRequest request) {

    Long userId = tokenService.getUserId(request);
    Page<IncomeDto> incomesDto;
    if (description == null) {
      incomesDto = incomeService.readAllByUser(userId, pageable);
    } else {
      incomesDto = incomeService.readByDescriptionAndUser(description, userId, pageable);
    }

    return ResponseEntity.status(HttpStatus.OK)
        .header("Content-Type", "application/json;charset=UTF-8")
        .body(incomesDto);
  }

  @Operation(summary = "Buscar receita por id", security = { @SecurityRequirement(name = "bearer-key") })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Encontrou a receita"),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "404", description = "Receita não encontrada", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class)) }),
  })
  @GetMapping("/{id}")
  public ResponseEntity<IncomeDto> readById(@PathVariable Long id, HttpServletRequest request) {
    Long userId = tokenService.getUserId(request);
    IncomeDto incomeDto = incomeService.readByIdAndUser(id, userId);

    return ResponseEntity.status(HttpStatus.OK)
        .header("Content-Type", "application/json;charset=UTF-8")
        .body(incomeDto);
  }

  @Operation(summary = "Buscar receitas do usuário por ano e mês", security = {
      @SecurityRequirement(name = "bearer-key") })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Encontrou receitas"),
      @ApiResponse(responseCode = "400", description = "Bad request - Algum parâmetro da requisição inválido", content = @Content),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "404", description = "Nenhuma receita encontrada", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class)) }),
  })
  @GetMapping("/{year}/{month}")
  public ResponseEntity<Page<IncomeDto>> readByYearAndMonth(
      @PathVariable Integer year,
      @PathVariable Integer month,
      @ParameterObject() @PageableDefault(sort = "date", direction = Direction.DESC, page = 0, size = 10) Pageable pageable,
      HttpServletRequest request) {
    Long userId = tokenService.getUserId(request);
    Page<IncomeDto> incomesDto = incomeService.readByYearAndMonthAndUser(year, month, userId, pageable);

    return ResponseEntity.status(HttpStatus.OK)
        .header("Content-Type", "application/json;charset=UTF-8")
        .body(incomesDto);
  }

  @Operation(summary = "Atualizar receita por id", security = { @SecurityRequirement(name = "bearer-key") })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Atualizado com sucesso"),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "400", description = "Bad request - Algum parâmetro do corpo da requisição inválido", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = FormErrorDto.class)) }),
      @ApiResponse(responseCode = "404", description = "Receita não encontrada", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class)) }),
      @ApiResponse(responseCode = "409", description = "Já existe receita com essa descrição no mesmo mês e ano", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class)) }),
  })
  @PutMapping("/{id}")
  @Transactional
  public ResponseEntity<IncomeDto> update(@PathVariable Long id, @Valid @RequestBody IncomeForm incomeForm,
      HttpServletRequest request) {
    Long userId = tokenService.getUserId(request);
    IncomeDto incomeDto = incomeService.update(id, incomeForm, userId);

    return ResponseEntity.status(HttpStatus.OK).header("Content-Type", "application/json;charset=UTF-8")
        .body(incomeDto);
  }

  @Operation(summary = "Deletar receita por id", security = { @SecurityRequirement(name = "bearer-key") })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Deletado com sucesso"),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "404", description = "Receita não encontrada", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class)) }),
  })
  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id, HttpServletRequest request) {
    Long userId = tokenService.getUserId(request);
    incomeService.delete(id, userId);
  }

}
