package br.com.emendes.financesapi.controller;

import br.com.emendes.financesapi.controller.dto.ExpenseDto;
import br.com.emendes.financesapi.controller.dto.error.ErrorDto;
import br.com.emendes.financesapi.controller.dto.error.FormErrorDto;
import br.com.emendes.financesapi.controller.form.ExpenseForm;
import br.com.emendes.financesapi.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/despesas")
public class ExpenseController {

  @Autowired
  private ExpenseService expenseService;

  private final String headerName = "Content-Type";
  private final String headerValue = "application/json;charset=UTF-8";

  @Operation(summary = "Salvar uma despesa", tags = { "Despesas" }, security = {
      @SecurityRequirement(name = "bearer-key") })
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
      UriComponentsBuilder uriBuilder) {
    ExpenseDto expenseDto = expenseService.create(form);
    URI uri = uriBuilder.path("/despesas/{id}").buildAndExpand(expenseDto.getId()).toUri();
    return ResponseEntity.created(uri).body(expenseDto);
  }

  @Operation(summary = "Buscar todas as despesas do usuário, opcional buscar por descrição", tags = {
      "Despesas" }, security = {
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
      @ParameterObject @PageableDefault(sort = "date", direction = Direction.DESC) Pageable pageable) {
    Page<ExpenseDto> expensesDto;

    if (description == null) {
      expensesDto = expenseService.readAllByUser(pageable);
    } else {
      expensesDto = expenseService.readByDescriptionAndUser(description, pageable);
    }
    return ResponseEntity.status(HttpStatus.OK)
        .header(headerName, headerValue)
        .body(expensesDto);
  }

  @Operation(summary = "Buscar despesa por id", tags = { "Despesas" }, security = {
      @SecurityRequirement(name = "bearer-key") })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Encontrou a despesa"),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "404", description = "Despesa não encontrada", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class)) }),
  })
  @GetMapping("/{id}")
  public ResponseEntity<ExpenseDto> readById(@PathVariable Long id) {
    ExpenseDto expenseDto = expenseService.readByIdAndUser(id);

    return ResponseEntity.status(HttpStatus.OK)
        .header(headerName, headerValue)
        .body(expenseDto);
  }

  @Operation(summary = "Buscar despesas do usuário por ano e mês", tags = { "Despesas" }, security = {
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
      @ParameterObject @PageableDefault(sort = "date", direction = Direction.DESC) Pageable pageable) {
    Page<ExpenseDto> expensesDto = expenseService.readByYearAndMonthAndUser(year, month, pageable);

    return ResponseEntity.status(HttpStatus.OK)
        .header(headerName, headerValue)
        .body(expensesDto);
  }

  @Operation(summary = "Atualizar despesa por id", tags = { "Despesas" }, security = {
      @SecurityRequirement(name = "bearer-key") })
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
  public ResponseEntity<ExpenseDto> update(@PathVariable Long id, @Valid @RequestBody ExpenseForm expenseForm) {
    ExpenseDto expenseDto = expenseService.update(id, expenseForm);

    return ResponseEntity.status(HttpStatus.OK)
        .header(headerName, headerValue)
        .body(expenseDto);
  }

  @Operation(summary = "Deletar despesa por id", tags = { "Despesas" }, security = {
      @SecurityRequirement(name = "bearer-key") })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Deletado com sucesso"),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "404", description = "Despesa não encontrada", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class)) }),
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    expenseService.delete(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
