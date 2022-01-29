package br.com.emendes.financesapi.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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

import br.com.emendes.financesapi.controller.dto.ExpenseDto;
import br.com.emendes.financesapi.controller.form.ExpenseForm;
import br.com.emendes.financesapi.model.Expense;
import br.com.emendes.financesapi.repository.ExpenseRepository;

// TODO: Refatorar e colocar alguns códigos/responsabilidades dentro de um service.
@RestController
@RequestMapping("/despesas")
public class ExpenseController {

  @Autowired
  ExpenseRepository expenseRepository;

  @PostMapping
  public ResponseEntity<ExpenseDto> create(@Valid @RequestBody ExpenseForm form, UriComponentsBuilder uriBuilder) {
    Expense expense = form.convert(expenseRepository);

    expenseRepository.save(expense);

    URI uri = uriBuilder.path("/despesas/{id}").buildAndExpand(expense.getId()).toUri();
    return ResponseEntity.created(uri).body(new ExpenseDto(expense));
  }

  @GetMapping
  public List<ExpenseDto> read(@RequestParam(required = false) String description) {
    List<Expense> expenses;
    if (description == null) {
      expenses = expenseRepository.findAll();
    } else {
      expenses = expenseRepository.findByDescription(description);
    }
    List<ExpenseDto> expensesDto = ExpenseDto.convert(expenses);

    return expensesDto;
  }

  @GetMapping("/{id}")
  public ResponseEntity<ExpenseDto> readById(@PathVariable Long id) {
    Optional<Expense> optional = expenseRepository.findById(id);
    if (optional.isPresent()) {
      ExpenseDto expenseDto = new ExpenseDto(optional.get());
      return ResponseEntity.ok(expenseDto);
    }

    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  }

  // TODO: Fazer tratamento caso o path não contenha um número para ano e mês.
  @GetMapping("/{year}/{month}")
  public List<ExpenseDto> readByYearAndMonth(@PathVariable Integer year, @PathVariable Integer month) {

    List<Expense> expenses = expenseRepository.findByYearAndMonth(year, month);

    List<ExpenseDto> expensesDto = ExpenseDto.convert(expenses);

    return expensesDto;
  }

  @PutMapping("/{id}")
  @Transactional
  public ResponseEntity<ExpenseDto> update(@PathVariable Long id, @Valid @RequestBody ExpenseForm expenseForm) {
    Optional<Expense> optional = expenseRepository.findById(id);
    if (optional.isPresent() && !expenseForm.exist(expenseRepository, id)) {
      Expense expense = optional.get();

      expense.setParams(expenseForm);

      return ResponseEntity.ok(new ExpenseDto(expense));
    }

    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete(@PathVariable Long id) {
    Optional<Expense> optional = expenseRepository.findById(id);
    if (optional.isPresent()) {
      expenseRepository.deleteById(id);
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  }

}
