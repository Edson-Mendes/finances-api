package br.com.emendes.financesapi.controller;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
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

import br.com.emendes.financesapi.controller.dto.ExpenseDto;
import br.com.emendes.financesapi.controller.dto.error.ErrorDto;
import br.com.emendes.financesapi.controller.dto.error.FormErrorDto;
import br.com.emendes.financesapi.controller.form.ExpenseForm;
import br.com.emendes.financesapi.service.ExpenseService;
import br.com.emendes.financesapi.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/despesas")
public class ExpenseController {

  @Autowired
  private ExpenseService expenseService;

  @Autowired
  private TokenService tokenService;

  @Operation(summary = "Salvar uma despesa", security = { @SecurityRequirement(name = "bearer-key") })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Despesa salva", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ExpenseDto.class)) }),
      @ApiResponse(responseCode = "400", description = "Bad request - Algum parâmetro do corpo da requisição inválido", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = FormErrorDto.class)) }),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "409", description = "Já existe despesa com essa descrição no mesmo mês e ano", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class)) }),
  })
  @PostMapping
  public ResponseEntity<ExpenseDto> create(@Valid @RequestBody ExpenseForm form,
      UriComponentsBuilder uriBuilder,
      HttpServletRequest request) {
    Long userId = tokenService.getUserId(request);
    return expenseService.create(form, userId, uriBuilder);
  }

  @Operation(summary = "Buscar todas as despesas do usuário, opcional buscar por descrição", security = {
      @SecurityRequirement(name = "bearer-key") })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Encontrou despesas"),
      @ApiResponse(responseCode = "400", description = "Bad request - Algum parâmetro da requisição inválido", content = @Content),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "404", description = "Nenhuma despesa encontrada", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class)) }),
  })
  @GetMapping
  public ResponseEntity<Page<ExpenseDto>> read(@RequestParam(required = false) String description,
      @ParameterObject @PageableDefault(sort = "date", direction = Direction.DESC, page = 0, size = 10) Pageable pageable,
      HttpServletRequest request) {
    Long userId = tokenService.getUserId(request);
    if (description == null) {
      return expenseService.readAllByUser(userId, pageable);
    } else {
      return expenseService.readByDescriptionAndUser(description, userId, pageable);
    }
  }

  @Operation(summary = "Buscar despesa por id", security = { @SecurityRequirement(name = "bearer-key") })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Encontrou a despesa"),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "404", description = "Despesa não encontrada", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class)) }),
  })
  @GetMapping("/{id}")
  public ResponseEntity<ExpenseDto> readById(@PathVariable Long id, HttpServletRequest request) {
    Long userId = tokenService.getUserId(request);
    return expenseService.readByIdAndUser(id, userId);
  }

  @Operation(summary = "Buscar despesas do usuário por ano e mês", security = {
      @SecurityRequirement(name = "bearer-key") })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Encontrou despesas"),
      @ApiResponse(responseCode = "400", description = "Bad request - Algum parâmetro da requisição inválido", content = @Content),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "404", description = "Nenhuma despesa encontrada", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class)) }),
  })
  @GetMapping("/{year}/{month}")
  public ResponseEntity<Page<ExpenseDto>> readByYearAndMonth(
      @PathVariable Integer year,
      @PathVariable Integer month,
      @ParameterObject @PageableDefault(sort = "date", direction = Direction.DESC, page = 0, size = 10) Pageable pageable,
      HttpServletRequest request) {
    Long userId = tokenService.getUserId(request);
    return expenseService.readByYearAndMonthAndUser(year, month, userId, pageable);
  }

  @Operation(summary = "Atualizar despesa por id", security = { @SecurityRequirement(name = "bearer-key") })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Atualizado com sucesso"),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "400", description = "Bad request - Algum parâmetro do corpo da requisição inválido", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = FormErrorDto.class)) }),
      @ApiResponse(responseCode = "404", description = "Despesa não encontrada", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class)) }),
      @ApiResponse(responseCode = "409", description = "Já existe despesa com essa descrição no mesmo mês e ano", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class)) }),
  })
  @PutMapping("/{id}")
  @Transactional
  public ResponseEntity<ExpenseDto> update(@PathVariable Long id, @Valid @RequestBody ExpenseForm expenseForm,
      HttpServletRequest request) {
    Long userId = tokenService.getUserId(request);
    return expenseService.update(id, expenseForm, userId);
  }

  @Operation(summary = "Deletar despesa por id", security = { @SecurityRequirement(name = "bearer-key") })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Deletado com sucesso"),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "404", description = "Despesa não encontrada", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class)) }),
  })
  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id, HttpServletRequest request) {
    Long userId = tokenService.getUserId(request);
    expenseService.delete(id, userId);
  }

}
