package br.com.emendes.financesapi.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import javax.persistence.NoResultException;

import br.com.emendes.financesapi.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

  public ExpenseDto create(ExpenseForm expenseForm) {
    alreadyExist(expenseForm);

    Long userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();

    Expense expense = expenseForm.convert(userId);
    expenseRepository.save(expense);

    return new ExpenseDto(expense);
  }

  public Page<ExpenseDto> readAllByUser(Pageable pageable) {
    Page<Expense> expenses = expenseRepository.findAllByUser(pageable);
    if (expenses.isEmpty()) {
      throw new NoResultException("O usuário não possui despesas");
    }
    return ExpenseDto.convert(expenses);
  }

  public Page<ExpenseDto> readByDescriptionAndUser(String description, Pageable pageable) {
    Page<Expense> expenses = expenseRepository.findByDescriptionAndUser(description, pageable);
    if (expenses.isEmpty()) {
      throw new NoResultException("O usuário não possui despesas com descrição similar a " + description);
    }
    return ExpenseDto.convert(expenses);
  }

  public ExpenseDto readByIdAndUser(Long id) {
    return new ExpenseDto(findByIdAndUser(id));
  }

  public Page<ExpenseDto> readByYearAndMonthAndUser(
      Integer year, Integer month, Pageable pageable) {
    Page<Expense> expenses = expenseRepository.findByYearAndMonthAndUser(year, month, pageable);

    if (expenses.isEmpty()) {
      throw new NoResultException(String.format("Não há despesas para o ano %d e mês %d", year, month));
    }
    return ExpenseDto.convert(expenses);
  }

  public ExpenseDto update(Long id, ExpenseForm expenseForm) {
    Expense expenseToBeUpdated = findByIdAndUser(id);
    alreadyExist(expenseForm, id);

    expenseToBeUpdated.setParams(expenseForm);
    return new ExpenseDto(expenseToBeUpdated);
  }

  public void deleteById(Long id) {
    findByIdAndUser(id);
    expenseRepository.deleteById(id);
  }

  public Optional<BigDecimal> getTotalValueByMonthAndYearAndUser(Integer year, Integer month) {
    return expenseRepository.getTotalValueByMonthAndYearAndUser(year, month);
  }

  public BigDecimal getTotalByCategoryOnYearAndMonth(Category category, Integer year, Integer month) {
    return expenseRepository.getTotalByCategoryOnYearAndMonth(category, year, month).orElse(BigDecimal.ZERO);
  }

  private Expense findByIdAndUser(Long id) {
    Optional<Expense> optionalExpense = expenseRepository.findByIdAndUser(id);

    return optionalExpense.orElseThrow(
        () -> new NoResultException(String.format("Nenhuma despesa com id = %d para esse usuário", id)));
  }

  /**
   * Verifica se o usuário já possui outra despesa com a mesma descrição no mesmo
   * mês e ano da respectiva despesa.
   * Forma que encontrei que impedir despesas com descrição duplicada no mesmo mês
   * e ano
   *
   * @param form
   * @return false, se não existir uma despesa com a mesma descrição em um mesmo
   * mês e ano.
   * @throws ResponseStatusException se existir despesa.
   */
//  TODO: Refatorar esse método
  private boolean alreadyExist(ExpenseForm form) {
    LocalDate date = form.parseDateToLocalDate();
    Optional<Expense> optional = expenseRepository.findByDescriptionAndMonthAndYearAndUser(
        form.getDescription(),
        date.getMonthValue(),
        date.getYear());
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
   * @param form
   * @param id
   * @return false, se não existir uma despesa com a mesma descrição em um mesmo
   * mês e ano.
   * @throws ResponseStatusException se existir despesa.
   */
  //  TODO: Refatorar esse método
  private boolean alreadyExist(ExpenseForm form, Long id) {
    LocalDate date = LocalDate.parse(form.getDate(), Formatter.dateFormatter);
    Optional<Expense> optional = expenseRepository.findByDescriptionAndMonthAndYearAndNotIdAndUser(
        form.getDescription(),
        date.getMonthValue(),
        date.getYear(),
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
