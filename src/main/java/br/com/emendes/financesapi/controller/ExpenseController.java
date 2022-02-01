package br.com.emendes.financesapi.controller;

import java.util.List;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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
import br.com.emendes.financesapi.controller.form.ExpenseForm;
import br.com.emendes.financesapi.service.ExpenseService;

@RestController
@RequestMapping("/despesas")
public class ExpenseController {

  @Autowired
  ExpenseService expenseService;

  @PostMapping
  public ResponseEntity<ExpenseDto> create(@Valid @RequestBody ExpenseForm form, UriComponentsBuilder uriBuilder) {
    return expenseService.create(form, uriBuilder);
  }

  @GetMapping
  public ResponseEntity<List<ExpenseDto>> read(@RequestParam(required = false) String description) {
    if (description == null) {
      return expenseService.readAll();
    } else {
      return expenseService.readByDescription(description);
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<ExpenseDto> readById(@PathVariable Long id) {
    return expenseService.readById(id);
  }

  // TODO: Fazer tratamento caso o path não contenha um número para ano e mês.
  @GetMapping("/{year}/{month}")
  public ResponseEntity<List<ExpenseDto>> readByYearAndMonth(@PathVariable Integer year, @PathVariable Integer month) {
    return expenseService.readByYearAndMonth(year, month);
  }

  @PutMapping("/{id}")
  @Transactional
  public ResponseEntity<ExpenseDto> update(@PathVariable Long id, @Valid @RequestBody ExpenseForm expenseForm) {
    return expenseService.update(id, expenseForm);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete(@PathVariable Long id) {
    return expenseService.delete(id);
  }

}
