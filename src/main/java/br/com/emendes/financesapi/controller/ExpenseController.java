package br.com.emendes.financesapi.controller;

import br.com.emendes.financesapi.controller.dto.ExpenseDto;
import br.com.emendes.financesapi.controller.form.ExpenseForm;
import br.com.emendes.financesapi.controller.openapi.ExpenseControllerOpenAPI;
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
@RequestMapping("/api/expenses")
public class ExpenseController implements ExpenseControllerOpenAPI {

  private final ExpenseService expenseService;

  private static final String HEADER_NAME = "Content-Type";
  private static final String HEADER_VALUE = "application/json;charset=UTF-8";

  @Override
  @PostMapping
  public ResponseEntity<ExpenseDto> create(@Valid @RequestBody ExpenseForm form, UriComponentsBuilder uriBuilder) {
    ExpenseDto expenseDto = expenseService.create(form);
    URI uri = uriBuilder.path("/despesas/{id}").buildAndExpand(expenseDto.getId()).toUri();
    return ResponseEntity.created(uri).body(expenseDto);
  }

  @Override
  @GetMapping
  public ResponseEntity<Page<ExpenseDto>> read(
      @RequestParam(name = "description", required = false) String description,
      @PageableDefault(sort = "date", direction = Direction.DESC) Pageable pageable) {
    Page<ExpenseDto> expensesDto;

    if (description == null) {
      expensesDto = expenseService.readAllByUser(pageable);
    } else {
      expensesDto = expenseService.readByDescriptionAndUser(description, pageable);
    }
    return ResponseEntity.status(HttpStatus.OK)
        .header(HEADER_NAME, HEADER_VALUE)
        .body(expensesDto);
  }

  @Override
  @GetMapping("/{id}")
  public ResponseEntity<ExpenseDto> readById(@PathVariable(name = "id") Long id) {
    ExpenseDto expenseDto = expenseService.readByIdAndUser(id);

    return ResponseEntity.status(HttpStatus.OK)
        .header(HEADER_NAME, HEADER_VALUE)
        .body(expenseDto);
  }

  @Override
  @GetMapping("/{year}/{month}")
  public ResponseEntity<Page<ExpenseDto>> readByYearAndMonth(
      @PathVariable(name = "year") int year,
      @PathVariable(name = "month") int month,
      @PageableDefault(sort = "date", direction = Direction.DESC) Pageable pageable) {
    Page<ExpenseDto> expensesDto = expenseService.readByYearAndMonthAndUser(year, month, pageable);

    return ResponseEntity.status(HttpStatus.OK)
        .header(HEADER_NAME, HEADER_VALUE)
        .body(expensesDto);
  }

  @Override
  @PutMapping("/{id}")
  @Transactional
  public ResponseEntity<ExpenseDto> update(
      @PathVariable(name = "id") Long id, @Valid @RequestBody ExpenseForm expenseForm) {
    ExpenseDto expenseDto = expenseService.update(id, expenseForm);

    return ResponseEntity.status(HttpStatus.OK)
        .header(HEADER_NAME, HEADER_VALUE)
        .body(expenseDto);
  }

  @Override
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable(name = "id") Long id) {
    expenseService.deleteById(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
