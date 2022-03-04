package br.com.emendes.financesapi.service;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.emendes.financesapi.config.validation.error_dto.ErrorDto;
import br.com.emendes.financesapi.controller.dto.ExpenseDto;
import br.com.emendes.financesapi.controller.form.ExpenseForm;
import br.com.emendes.financesapi.model.Expense;
import br.com.emendes.financesapi.model.enumerator.Category;
import br.com.emendes.financesapi.repository.ExpenseRepository;

@Service
public class ExpenseService {

  @Autowired
  ExpenseRepository expenseRepository;

  public ResponseEntity<ExpenseDto> create(ExpenseForm form, UriComponentsBuilder uriBuilder) {
    Expense expense = form.convert(expenseRepository);

    expenseRepository.save(expense);

    URI uri = uriBuilder.path("/despesas/{id}").buildAndExpand(expense.getId()).toUri();
    return ResponseEntity.created(uri).body(new ExpenseDto(expense));
  }

  public ResponseEntity<List<ExpenseDto>> readAll() {
    List<Expense> expenses = expenseRepository.findAll();
    List<ExpenseDto> expensesDto = ExpenseDto.convert(expenses);

    return ResponseEntity.ok(expensesDto);
  }

  public ResponseEntity<List<ExpenseDto>> readByDescription(String description) {
    List<Expense> expenses = expenseRepository.findByDescription(description);
    if (expenses.isEmpty() || expenses == null) {
      return ResponseEntity.notFound().build();
    }

    List<ExpenseDto> expensesDto = ExpenseDto.convert(expenses);
    return ResponseEntity.ok(expensesDto);
  }

  public ResponseEntity<ExpenseDto> readById(Long id) {
    Optional<Expense> optional = expenseRepository.findById(id);
    if (optional.isPresent()) {
      ExpenseDto expenseDto = new ExpenseDto(optional.get());
      return ResponseEntity.ok(expenseDto);
    }

    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  }


  public ResponseEntity<?> readByYearAndMonth(Integer year, Integer month) {
    List<Expense> expenses = expenseRepository.findByYearAndMonth(year, month);

    if (expenses.isEmpty() || expenses == null) {
      String message = "Não há despesas para o ano " + year + " e mês " + month;
      ErrorDto errorDto = new ErrorDto("Not Found", message);
      return ResponseEntity
          .status(HttpStatus.NOT_FOUND)
          .header("Content-Type", "application/json;charset=UTF-8")
          .body(errorDto);
    }
    List<ExpenseDto> expensesDto = ExpenseDto.convert(expenses);
    return ResponseEntity.ok(expensesDto);
  }

  public ResponseEntity<ExpenseDto> update(Long id, ExpenseForm expenseForm) {
    Optional<Expense> optional = expenseRepository.findById(id);
    if (optional.isPresent() && !expenseForm.exist(expenseRepository, id)) {
      Expense expense = optional.get();

      expense.setParams(expenseForm);

      return ResponseEntity.ok(new ExpenseDto(expense));
    }

    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  }

  public ResponseEntity<?> delete(Long id) {
    Optional<Expense> optional = expenseRepository.findById(id);
    if (optional.isPresent()) {
      expenseRepository.deleteById(id);
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  }

  public BigDecimal getTotalValueByMonthAndYear(Integer year, Integer month) {
    if (expenseRepository.getTotalValueByMonthAndYear(year, month).isPresent()) {
      return expenseRepository.getTotalValueByMonthAndYear(year, month).get();
    }
    throw new RuntimeException("Total value not found for year: " + year + " e month: " + month);
  }

  public BigDecimal getTotalByCategoryInYearAndMonth(Category category, Integer year, Integer month) {
    return expenseRepository.getTotalByCategoryInYearAndMonth(category, year, month).orElse(BigDecimal.ZERO);
  }

}
