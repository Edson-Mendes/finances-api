package br.com.emendes.financesapi.controller;

import br.com.emendes.financesapi.controller.openapi.ExpenseControllerOpenAPI;
import br.com.emendes.financesapi.dto.request.ExpenseRequest;
import br.com.emendes.financesapi.dto.response.ExpenseResponse;
import br.com.emendes.financesapi.service.ExpenseService;
import lombok.RequiredArgsConstructor;
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

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/expenses", produces = "application/json;charset=UTF-8")
public class ExpenseController implements ExpenseControllerOpenAPI {

  private final ExpenseService expenseService;

  @Override
  @PostMapping
  public ResponseEntity<ExpenseResponse> create(@Valid @RequestBody ExpenseRequest form, UriComponentsBuilder uriBuilder) {
    ExpenseResponse expenseResponse = expenseService.create(form);
    URI uri = uriBuilder.path("/api/expenses/{id}").buildAndExpand(expenseResponse.getId()).toUri();
    return ResponseEntity.created(uri).body(expenseResponse);
  }

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

  @Override
  @GetMapping("/{id}")
  public ResponseEntity<ExpenseResponse> readById(@PathVariable(name = "id") Long id) {
    ExpenseResponse expenseResponse = expenseService.readByIdAndUser(id);

    return ResponseEntity.status(HttpStatus.OK)
        .body(expenseResponse);
  }

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

  @Override
  @PutMapping("/{id}")
  @Transactional
  public ResponseEntity<ExpenseResponse> update(
      @PathVariable(name = "id") Long id, @Valid @RequestBody ExpenseRequest expenseRequest) {
    ExpenseResponse expenseResponse = expenseService.update(id, expenseRequest);

    return ResponseEntity.status(HttpStatus.OK)
        .body(expenseResponse);
  }

  @Override
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable(name = "id") Long id) {
    expenseService.deleteById(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
