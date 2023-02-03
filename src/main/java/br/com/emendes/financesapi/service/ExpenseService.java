package br.com.emendes.financesapi.service;

import br.com.emendes.financesapi.controller.dto.ExpenseDto;
import br.com.emendes.financesapi.controller.dto.ValueByCategoryDto;
import br.com.emendes.financesapi.controller.form.ExpenseForm;
import br.com.emendes.financesapi.model.entity.Expense;
import br.com.emendes.financesapi.model.entity.User;
import br.com.emendes.financesapi.repository.ExpenseRepository;
import br.com.emendes.financesapi.util.Formatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.NoResultException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {

  @Autowired
  private ExpenseRepository expenseRepository;

  public ExpenseDto create(ExpenseForm expenseForm) {
    existsIncomeWithSameDescriptionOnMonthYear(expenseForm);

    Long userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();

    Expense expense = expenseForm.convert(userId);
    expenseRepository.save(expense);

    return new ExpenseDto(expense);
  }

  public Page<ExpenseDto> readAllByUser(Pageable pageable) {
    Page<Expense> expenses = expenseRepository.findAllByUser(pageable);
    if (expenses.getTotalElements() == 0) {
      throw new NoResultException("O usuário não possui despesas");
    }
    return ExpenseDto.convert(expenses);
  }

  public Page<ExpenseDto> readByDescriptionAndUser(String description, Pageable pageable) {
    Page<Expense> expenses = expenseRepository.findByDescriptionAndUser(description, pageable);
    if (expenses.getTotalElements() == 0) {
      throw new NoResultException("O usuário não possui despesas com descrição similar a " + description);
    }
    return ExpenseDto.convert(expenses);
  }

  public ExpenseDto readByIdAndUser(Long id) {
    return new ExpenseDto(findByIdAndUser(id));
  }

  public Page<ExpenseDto> readByYearAndMonthAndUser(
      int year, int month, Pageable pageable) {
    Page<Expense> expenses = expenseRepository.findByYearAndMonthAndUser(year, month, pageable);

    if (expenses.getTotalElements() == 0) {
      throw new NoResultException(String.format("Não há despesas para o ano %d e mês %d", year, month));
    }
    return ExpenseDto.convert(expenses);
  }

  public ExpenseDto update(Long id, ExpenseForm expenseForm) {
    Expense expenseToBeUpdated = findByIdAndUser(id);
    existsAnotherExpenseWithSameDescriptionOnMonthYear(expenseForm, id);

    expenseToBeUpdated.setParams(expenseForm);
    return new ExpenseDto(expenseToBeUpdated);
  }

  public void deleteById(Long id) {
    findByIdAndUser(id);
    expenseRepository.deleteById(id);
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
  private boolean existsIncomeWithSameDescriptionOnMonthYear(ExpenseForm form) {
    LocalDate date = form.parseDateToLocalDate();
    boolean exists = expenseRepository.existsByDescriptionAndMonthAndYearAndUser(
        form.getDescription(),
        date.getMonthValue(),
        date.getYear());
    if (exists) {
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
  private boolean existsAnotherExpenseWithSameDescriptionOnMonthYear(ExpenseForm form, Long id) {
    LocalDate date = LocalDate.parse(form.getDate(), Formatter.dateFormatter);
    boolean exists = expenseRepository.existsByDescriptionAndMonthAndYearAndNotIdAndUser(
        form.getDescription(),
        date.getMonthValue(),
        date.getYear(),
        id);
    if (exists) {
      String message = "Outra despesa com essa descrição já existe em "  + date.getMonth().name().toLowerCase() + " "
          + date.getYear();
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, message, null);
    }
    return false;
  }

  public List<ValueByCategoryDto> getValuesByCategoryOnMonthAndYearByUser(Integer year, Integer month) {
    return expenseRepository.getValueByCategoryAndMonthAndYearAndUser(year, month);
  }
}
