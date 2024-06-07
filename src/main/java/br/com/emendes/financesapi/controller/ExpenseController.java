package br.com.emendes.financesapi.controller;

import br.com.emendes.financesapi.controller.openapi.ExpenseControllerOpenAPI;
import br.com.emendes.financesapi.dto.request.ExpenseRequest;
import br.com.emendes.financesapi.dto.response.ExpenseResponse;
import br.com.emendes.financesapi.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * Classe controller para lidar com os endpoints /api/expenses/**.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/expenses", produces = "application/json;charset=UTF-8")
public class ExpenseController implements ExpenseControllerOpenAPI {

  private final ExpenseService expenseService;

  /**
   * Método responsável por POST /api/expenses.
   *
   * @param expenseRequest objeto contendo os dados de registro de despesa.
   */
  @Override
  @PostMapping
  public ResponseEntity<ExpenseResponse> create(
      @Valid @RequestBody ExpenseRequest expenseRequest, UriComponentsBuilder uriBuilder) {
    ExpenseResponse expenseResponse = expenseService.create(expenseRequest);
    URI uri = uriBuilder.path("/api/expenses/{id}").buildAndExpand(expenseResponse.getId()).toUri();
    return ResponseEntity.created(uri).body(expenseResponse);
  }

  /**
   * Método responsável por GET /api/expenses.
   *
   * @param description descrição das despesas a serem buscadas, parâmetro opcional.
   */
  @Override
  @GetMapping
  public ResponseEntity<Page<ExpenseResponse>> read(
      @RequestParam(name = "description", required = false) String description,
      @PageableDefault(sort = "date", direction = Direction.DESC) Pageable pageable) {
    Page<ExpenseResponse> expensesDto;

    if (description == null) {
      expensesDto = expenseService.readAllByUser(pageable);
    } else {
      expensesDto = expenseService.readByDescriptionAndUser(description, pageable);
    }
    return ResponseEntity.status(HttpStatus.OK)
        .body(expensesDto);
  }

  /**
   * Método responsável por GET /api/expenses/{id}.
   *
   * @param id identificador da despesa a ser buscada.
   */
  @Override
  @GetMapping("/{id}")
  public ResponseEntity<ExpenseResponse> readById(@PathVariable(name = "id") Long id) {
    ExpenseResponse expenseResponse = expenseService.readByIdAndUser(id);

    return ResponseEntity.status(HttpStatus.OK)
        .body(expenseResponse);
  }

  /**
   * Método responsável por GET /api/expenses/{year}/{month}.
   *
   * @param year  ano das despesas a serem buscadas.
   * @param month mês das despesas a serem buscadas.
   */
  @Override
  @GetMapping("/{year}/{month}")
  public ResponseEntity<Page<ExpenseResponse>> readByYearAndMonth(
      @PathVariable(name = "year") int year,
      @PathVariable(name = "month") int month,
      @PageableDefault(sort = "date", direction = Direction.DESC) Pageable pageable) {
    Page<ExpenseResponse> expensesDto = expenseService.readByYearAndMonthAndUser(year, month, pageable);

    return ResponseEntity.status(HttpStatus.OK)
        .body(expensesDto);
  }

  /**
   * Método responsável por PUT /api/expenses/{id}.
   *
   * @param id             identificador da despesa a ser atualizada.
   * @param expenseRequest objeto com os novos dados da despesa.
   */
  @Override
  @PutMapping("/{id}")
  public ResponseEntity<ExpenseResponse> update(
      @PathVariable(name = "id") Long id, @Valid @RequestBody ExpenseRequest expenseRequest) {
    ExpenseResponse expenseResponse = expenseService.update(id, expenseRequest);

    return ResponseEntity.status(HttpStatus.OK)
        .body(expenseResponse);
  }

  /**
   * Método responsável por DELETE /api/expenses/{id}.
   *
   * @param id identificador da despesa a ser deletada.
   */
  @Override
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable(name = "id") Long id) {
    expenseService.deleteById(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
