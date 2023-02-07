package br.com.emendes.financesapi.controller;

import br.com.emendes.financesapi.controller.dto.IncomeDto;
import br.com.emendes.financesapi.controller.form.IncomeForm;
import br.com.emendes.financesapi.controller.openapi.IncomeControllerOpenAPI;
import br.com.emendes.financesapi.service.IncomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.net.URI;

@Validated
@RestController
@RequestMapping("/api/incomes")
public class IncomeController implements IncomeControllerOpenAPI {

  @Autowired
  private IncomeService incomeService;
  private final String headerName = "Content-Type";
  private final String headerValue = "application/json;charset=UTF-8";

  @Override
  @PostMapping
  public ResponseEntity<IncomeDto> create(@Valid @RequestBody IncomeForm form, UriComponentsBuilder uriBuilder) {
    IncomeDto incomeDto = incomeService.create(form);
    URI uri = uriBuilder.path("/despesas/{id}").buildAndExpand(incomeDto.getId()).toUri();
    return ResponseEntity.created(uri).body(incomeDto);
  }

  @Override
  @GetMapping
  public ResponseEntity<Page<IncomeDto>> read(
      @RequestParam(required = false) String description,
      @PageableDefault(sort = "date", direction = Direction.DESC, page = 0, size = 10) Pageable pageable) {

    Page<IncomeDto> incomesDto;
    if (description == null) {
      incomesDto = incomeService.readAllByUser(pageable);
    } else {
      incomesDto = incomeService.readByDescriptionAndUser(description, pageable);
    }

    return ResponseEntity.status(HttpStatus.OK)
        .header(headerName, headerValue)
        .body(incomesDto);
  }

  @Override
  @GetMapping("/{id}")
  public ResponseEntity<IncomeDto> readById(@PathVariable Long id) {
    IncomeDto incomeDto = incomeService.readByIdAndUser(id);
    return ResponseEntity.status(HttpStatus.OK)
        .header(headerName, headerValue)
        .body(incomeDto);
  }

  @Override
  @GetMapping("/{year}/{month}")
  public ResponseEntity<Page<IncomeDto>> readByYearAndMonth(
      @Min(1970) @Max(2099) @PathVariable int year,
      @Min(1) @Max(12) @PathVariable int month,
      @PageableDefault(sort = "date", direction = Direction.DESC, page = 0, size = 10) Pageable pageable) {
    Page<IncomeDto> incomesDto = incomeService.readByYearAndMonthAndUser(year, month, pageable);
    return ResponseEntity.status(HttpStatus.OK)
        .header(headerName, headerValue)
        .body(incomesDto);
  }

  @Override
  @PutMapping("/{id}")
  @Transactional
  public ResponseEntity<IncomeDto> update(@PathVariable Long id, @Valid @RequestBody IncomeForm incomeForm) {

    IncomeDto incomeDto = incomeService.update(id, incomeForm);
    return ResponseEntity.status(HttpStatus.OK).header(headerName, headerValue)
        .body(incomeDto);
  }

  @Override
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    incomeService.deleteById(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
