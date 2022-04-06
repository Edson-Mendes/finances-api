package br.com.emendes.financesapi.service;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.Optional;

import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.emendes.financesapi.controller.dto.ExpenseDto;
import br.com.emendes.financesapi.controller.form.ExpenseForm;
import br.com.emendes.financesapi.model.Expense;
import br.com.emendes.financesapi.model.enumerator.Category;
import br.com.emendes.financesapi.repository.ExpenseRepository;
import br.com.emendes.financesapi.util.Formatter;

@Service
public class ExpenseService {

  @Autowired
  private ExpenseRepository expenseRepository;

  public ResponseEntity<ExpenseDto> create(ExpenseForm expenseForm, Long userId, UriComponentsBuilder uriBuilder) {
    alreadyExist(expenseForm, userId);

    Expense expense = expenseForm.convert(userId);
    expenseRepository.save(expense);

    URI uri = uriBuilder.path("/despesas/{id}").buildAndExpand(expense.getId()).toUri();
    return ResponseEntity.created(uri).body(new ExpenseDto(expense));
  }

  public ResponseEntity<Page<ExpenseDto>> readAllByUser(Long userId, Pageable pageable) {
    Page<Expense> expenses = expenseRepository.findByUserId(userId, pageable);
    if (expenses.getTotalElements() == 0) {
      throw new NoResultException("O usuário não possui despesas");
    }
    Page<ExpenseDto> expensesDto = ExpenseDto.convert(expenses);

    return ResponseEntity.status(HttpStatus.OK)
        .header("Content-Type", "application/json;charset=UTF-8")
        .body(expensesDto);
  }

  public ResponseEntity<Page<ExpenseDto>> readByDescriptionAndUser(String description, Long userId, Pageable pageable) {
    Page<Expense> expenses = expenseRepository.findByDescriptionAndUserId(description, userId, pageable);
    if (expenses.getTotalElements() == 0) {
      throw new NoResultException("O usuário não possui despesas com descrição similar a " + description);
    }

    Page<ExpenseDto> expensesDto = ExpenseDto.convert(expenses);
    return ResponseEntity.status(HttpStatus.OK)
        .header("Content-Type", "application/json;charset=UTF-8")
        .body(expensesDto);
  }

  public ResponseEntity<ExpenseDto> readByIdAndUser(Long id, Long userId) {
    Optional<Expense> optional = expenseRepository.findByIdAndUserId(id, userId);
    if (optional.isEmpty()) {
      throw new NoResultException("Nenhuma despesa com esse id para esse usuário");
    }
    ExpenseDto expenseDto = new ExpenseDto(optional.get());
    return ResponseEntity.status(HttpStatus.OK)
        .header("Content-Type", "application/json;charset=UTF-8")
        .body(expenseDto);
  }

  public ResponseEntity<Page<ExpenseDto>> readByYearAndMonthAndUser(
      Integer year,
      Integer month,
      Long userId,
      Pageable pageable) {
    Page<Expense> expenses = expenseRepository.findByYearAndMonthAndUserId(year, month, userId, pageable);

    if (expenses.getTotalElements() == 0) {
      throw new NoResultException("Não há despesas para o ano " + year + " e mês " + month);
    }
    Page<ExpenseDto> expensesDto = ExpenseDto.convert(expenses);
    return ResponseEntity.status(HttpStatus.OK)
        .header("Content-Type", "application/json;charset=UTF-8")
        .body(expensesDto);
  }

  public ResponseEntity<ExpenseDto> update(Long id, ExpenseForm expenseForm, Long userId) {
    Optional<Expense> optional = expenseRepository.findByIdAndUserId(id, userId);
    if (optional.isPresent() && !alreadyExist(expenseForm, id, userId)) {
      Expense expense = optional.get();
      expense.setParams(expenseForm);

      return ResponseEntity.status(HttpStatus.OK)
          .header("Content-Type", "application/json;charset=UTF-8")
          .body(new ExpenseDto(expense));
    }

    throw new NoResultException("Nenhuma despesa com esse id para esse usuário");
  }

  public void delete(Long id, Long userId) {
    Optional<Expense> optional = expenseRepository.findByIdAndUserId(id, userId);
    if (optional.isEmpty()) {
      throw new NoResultException("Nenhuma despesa com esse id para esse usuário");
    }
    expenseRepository.deleteById(id);
  }

  public Optional<BigDecimal> getTotalValueByMonthAndYearAndUserId(Integer year, Integer month, Long userId) {
    return expenseRepository.getTotalValueByMonthAndYearAndUserId(year, month, userId);
  }

  public BigDecimal getTotalByCategoryOnYearAndMonth(Category category, Integer year, Integer month, Long userId) {
    return expenseRepository.getTotalByCategoryOnYearAndMonth(category, year, month, userId).orElse(BigDecimal.ZERO);
  }

  /**
   * Verifica se o usuário já possui outra despesa com a mesma descrição no mesmo
   * mês e ano da respectiva despesa.
   * Forma que encontrei que impedir despesas com descrição duplicada no mesmo mês
   * e ano
   * 
   * @param expenseRepository
   * @param userId
   * @return false, se não existir uma despesa com a mesma descrição em um mesmo
   *         mês e ano.
   * @throws ResponseStatusException se existir despesa.
   */
  private boolean alreadyExist(ExpenseForm form, Long userId) {
    LocalDate date = LocalDate.parse(form.getDate(), Formatter.dateFormatter);
    Optional<Expense> optional = expenseRepository.findByDescriptionAndMonthAndYearAndUserId(
        form.getDescription(),
        date.getMonthValue(),
        date.getYear(),
        userId);
    if (optional.isPresent()) {
      String message = "Uma despesa com essa descrição já existe em " + date.getMonth().name().toLowerCase() + " "
          + date.getYear();
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, message, null);
    }
    return false;
  }

  /**
   * Verifica se o usuário já possui outra despesa com a mesma descrição no mesmo
   * mês e
   * ano da respectiva despesa.
   * e com id diferente do atual.
   * 
   * @param expenseRepository
   * @param id
   * @param userId
   * @return false, se não existir uma despesa com a mesma descrição em um mesmo
   *         mês e ano.
   * @throws ResponseStatusException se existir despesa.
   */
  private boolean alreadyExist(ExpenseForm form, Long id, Long userId) {
    LocalDate date = LocalDate.parse(form.getDate(), Formatter.dateFormatter);
    Optional<Expense> optional = expenseRepository.findByDescriptionAndMonthAndYearAndUserIdAndNotId(
        form.getDescription(),
        date.getMonthValue(),
        date.getYear(),
        userId,
        id);
    if (optional.isPresent()) {
      String message = "Descrição de despesa duplicada para " + date.getMonth().name().toLowerCase() + " "
          + date.getYear();
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, message, null);
    }
    return false;
  }
}
