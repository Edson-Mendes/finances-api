package br.com.emendes.financesapi.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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

//  TODO: Usar Principal para o usuário atual.
  public ExpenseDto create(ExpenseForm expenseForm, Long userId) {
    alreadyExist(expenseForm, userId);

    Expense expense = expenseForm.convert(userId);
    expenseRepository.save(expense);

    return new ExpenseDto(expense);
  }

  public Page<ExpenseDto> readAllByUser(Long userId, Pageable pageable) {
    Page<Expense> expenses = expenseRepository.findAllByUser(pageable);
    if (expenses.isEmpty()) {
      throw new NoResultException("O usuário não possui despesas");
    }
    return ExpenseDto.convert(expenses);
  }

  public Page<ExpenseDto> readByDescriptionAndUser(String description, Long userId, Pageable pageable) {
    Page<Expense> expenses = expenseRepository.findByDescriptionAndUser(description, pageable);
    if (expenses.isEmpty()) {
      throw new NoResultException("O usuário não possui despesas com descrição similar a " + description);
    }
    return ExpenseDto.convert(expenses);
  }

  public ExpenseDto readByIdAndUser(Long id, Long userId) {
    Optional<Expense> optional = expenseRepository.findByIdAndUser(id);
    return new ExpenseDto(optional.orElseThrow(() -> {
      throw new NoResultException(String.format("Nenhuma despesa com id = %d para esse usuário", id));
    }));
  }

  public Page<ExpenseDto> readByYearAndMonthAndUser(
      Integer year, Integer month,
      Long userId, Pageable pageable) {
    Page<Expense> expenses = expenseRepository.findByYearAndMonthAndUser(year, month, pageable);

    if (expenses.isEmpty()) {
      throw new NoResultException(String.format("Não há despesas para o ano %d e mês %d", year, month));
    }
    return ExpenseDto.convert(expenses);
  }

  public ExpenseDto update(Long id, ExpenseForm expenseForm, Long userId) {
    Optional<Expense> optional = expenseRepository.findByIdAndUser(id);
    if (optional.isPresent() && !alreadyExist(expenseForm, id, userId)) {
      Expense expense = optional.get();
      expense.setParams(expenseForm);

      return new ExpenseDto(expense);
    }

    throw new NoResultException("Nenhuma despesa com esse id para esse usuário");
  }

  public void delete(Long id, Long userId) {
    // TODO: Talvez chamar o delete por id e userId direto e lançar uma exception se
    // não for possível.
    Optional<Expense> optional = expenseRepository.findByIdAndUser(id);
    if (optional.isEmpty()) {
      throw new NoResultException("Nenhuma despesa com esse id para esse usuário");
    }
    expenseRepository.deleteById(id);
  }

  public Optional<BigDecimal> getTotalValueByMonthAndYearAndUserId(Integer year, Integer month, Long userId) {
    return expenseRepository.getTotalValueByMonthAndYearAndUser(year, month);
  }

  public BigDecimal getTotalByCategoryOnYearAndMonth(Category category, Integer year, Integer month, Long userId) {
    return expenseRepository.getTotalByCategoryOnYearAndMonth(category, year, month).orElse(BigDecimal.ZERO);
  }

  /**
   * Verifica se o usuário já possui outra despesa com a mesma descrição no mesmo
   * mês e ano da respectiva despesa.
   * Forma que encontrei que impedir despesas com descrição duplicada no mesmo mês
   * e ano
   *
   * @param form
   * @param userId
   * @return false, se não existir uma despesa com a mesma descrição em um mesmo
   *         mês e ano.
   * @throws ResponseStatusException se existir despesa.
   */
  private boolean alreadyExist(ExpenseForm form, Long userId) {
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
   * @param userId
   * @return false, se não existir uma despesa com a mesma descrição em um mesmo
   *         mês e ano.
   * @throws ResponseStatusException se existir despesa.
   */
  private boolean alreadyExist(ExpenseForm form, Long id, Long userId) {
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
