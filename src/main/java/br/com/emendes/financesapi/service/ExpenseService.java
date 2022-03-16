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
import br.com.emendes.financesapi.model.User;
import br.com.emendes.financesapi.model.enumerator.Category;
import br.com.emendes.financesapi.repository.ExpenseRepository;
import br.com.emendes.financesapi.repository.UserRepository;

@Service
public class ExpenseService {

  @Autowired
  private ExpenseRepository expenseRepository;

  @Autowired
  private UserRepository userRepository;

  public ResponseEntity<ExpenseDto> create(ExpenseForm form, Long userId, UriComponentsBuilder uriBuilder) {
    User user = userRepository.findById(userId).get();
    Expense expense = form.convert(expenseRepository, userId);
    expense.setUser(user);

    expenseRepository.save(expense);

    URI uri = uriBuilder.path("/despesas/{id}").buildAndExpand(expense.getId()).toUri();
    return ResponseEntity.created(uri).body(new ExpenseDto(expense));
  }

  public ResponseEntity<?> readAllByUser(Long userId) {
    List<Expense> expenses = expenseRepository.findByUserId(userId);
    if (expenses.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .header("Content-Type", "application/json;charset=UTF-8")
          .body(new ErrorDto("Not Found", "O usuário não possui despesas"));
    }
    List<ExpenseDto> expensesDto = ExpenseDto.convert(expenses);

    return ResponseEntity.status(HttpStatus.OK)
        .header("Content-Type", "application/json;charset=UTF-8")
        .body(expensesDto);
  }

  public ResponseEntity<?> readByDescriptionAndUser(String description, Long userId) {
    List<Expense> expenses = expenseRepository.findByDescriptionAndUserId(description, userId);
    if (expenses.isEmpty() || expenses == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .header("Content-Type", "application/json;charset=UTF-8")
          .body(new ErrorDto("Not Found", "O usuário não possui despesas com descrição similar a " + description));

    }

    List<ExpenseDto> expensesDto = ExpenseDto.convert(expenses);
    return ResponseEntity.status(HttpStatus.OK)
        .header("Content-Type", "application/json;charset=UTF-8")
        .body(expensesDto);
  }

  public ResponseEntity<?> readByIdAndUser(Long id, Long userId) {
    Optional<Expense> optional = expenseRepository.findByIdAndUserId(id, userId);
    if (optional.isPresent()) {
      ExpenseDto expenseDto = new ExpenseDto(optional.get());
      return ResponseEntity.status(HttpStatus.OK)
          .header("Content-Type", "application/json;charset=UTF-8")
          .body(expenseDto);
    }

    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .header("Content-Type", "application/json;charset=UTF-8")
        .body(new ErrorDto("Not Found", "Nenhuma despesa com esse id para esse usuário"));
  }

  public ResponseEntity<?> readByYearAndMonthAndUser(Integer year, Integer month, Long userId) {
    List<Expense> expenses = expenseRepository.findByYearAndMonthAndUserId(year, month, userId);

    if (expenses.isEmpty() || expenses == null) {
      String message = "Não há despesas para o ano " + year + " e mês " + month;
      ErrorDto errorDto = new ErrorDto("Not Found", message);
      return ResponseEntity
          .status(HttpStatus.NOT_FOUND)
          .header("Content-Type", "application/json;charset=UTF-8")
          .body(errorDto);
    }
    List<ExpenseDto> expensesDto = ExpenseDto.convert(expenses);
    return ResponseEntity.status(HttpStatus.OK)
        .header("Content-Type", "application/json;charset=UTF-8")
        .body(expensesDto);
  }

  public ResponseEntity<?> update(Long id, ExpenseForm expenseForm, Long userId) {
    Optional<Expense> optional = expenseRepository.findByIdAndUserId(id, userId);
    if (optional.isPresent() && !expenseForm.exist(expenseRepository, id, userId)) {
      Expense expense = optional.get();

      expense.setParams(expenseForm);

      return ResponseEntity.status(HttpStatus.OK)
          .header("Content-Type", "application/json;charset=UTF-8")
          .body(new ExpenseDto(expense));
    }

    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .header("Content-Type", "application/json;charset=UTF-8")
        .body(new ErrorDto("Not Found", "Nenhuma despesa com esse id para esse usuário"));
  }

  public ResponseEntity<?> delete(Long id, Long userId) {
    Optional<Expense> optional = expenseRepository.findByIdAndUserId(id, userId);
    if (optional.isPresent()) {
      expenseRepository.deleteById(id);
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .header("Content-Type", "application/json;charset=UTF-8")
        .body(new ErrorDto("Not Found", "Nenhuma despesa com esse id para esse usuário"));
  }

  public Optional<BigDecimal> getTotalValueByMonthAndYearAndUserId(Integer year, Integer month, Long userId) {
    return expenseRepository.getTotalValueByMonthAndYearAndUserId(year, month, userId);
  }

  public BigDecimal getTotalByCategoryOnYearAndMonth(Category category, Integer year, Integer month, Long userId) {
    return expenseRepository.getTotalByCategoryOnYearAndMonth(category, year, month, userId).orElse(BigDecimal.ZERO);
  }

}
