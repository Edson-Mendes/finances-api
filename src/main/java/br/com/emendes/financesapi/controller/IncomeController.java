package br.com.emendes.financesapi.controller;

import br.com.emendes.financesapi.dto.response.IncomeResponse;
import br.com.emendes.financesapi.dto.request.IncomeRequest;
import br.com.emendes.financesapi.controller.openapi.IncomeControllerOpenAPI;
import br.com.emendes.financesapi.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/incomes", produces = "application/json;charset=UTF-8")
public class IncomeController implements IncomeControllerOpenAPI {

  private final IncomeService incomeService;

  @Override
  @PostMapping
  public ResponseEntity<IncomeResponse> create(@Valid @RequestBody IncomeRequest form, UriComponentsBuilder uriBuilder) {
    IncomeResponse incomeResponse = incomeService.create(form);
    URI uri = uriBuilder.path("/api/incomes/{id}").buildAndExpand(incomeResponse.getId()).toUri();
    return ResponseEntity.created(uri).body(incomeResponse);
  }

  @Override
  @GetMapping
  public ResponseEntity<Page<IncomeResponse>> read(
      @RequestParam(required = false) String description,
      @PageableDefault(sort = "date", direction = Direction.DESC) Pageable pageable) {

    Page<IncomeResponse> incomesDto;
    if (description == null) {
      incomesDto = incomeService.readAllByUser(pageable);
    } else {
      incomesDto = incomeService.readByDescriptionAndUser(description, pageable);
    }

    return ResponseEntity.status(HttpStatus.OK)
        .body(incomesDto);
  }

  @Override
  @GetMapping("/{id}")
  public ResponseEntity<IncomeResponse> readById(@PathVariable(name = "id") Long id) {
    IncomeResponse incomeResponse = incomeService.readByIdAndUser(id);
    return ResponseEntity.status(HttpStatus.OK)
        .body(incomeResponse);
  }

  @Override
  @GetMapping("/{year}/{month}")
  public ResponseEntity<Page<IncomeResponse>> readByYearAndMonth(
      @PathVariable(name = "year") int year,
      @PathVariable(name = "month") int month,
      @PageableDefault(sort = "date", direction = Direction.DESC) Pageable pageable) {
    Page<IncomeResponse> incomesDto = incomeService.readByYearAndMonthAndUser(year, month, pageable);
    return ResponseEntity.status(HttpStatus.OK)
        .body(incomesDto);
  }

  @Override
  @PutMapping("/{id}")
  public ResponseEntity<IncomeResponse> update(@PathVariable(name = "id") long id, @RequestBody @Valid IncomeRequest incomeRequest) {
    IncomeResponse incomeResponse = incomeService.update(id, incomeRequest);
    return ResponseEntity.status(HttpStatus.OK)
        .body(incomeResponse);
  }

  @Override
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable(name = "id") Long id) {
    incomeService.deleteById(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

}
