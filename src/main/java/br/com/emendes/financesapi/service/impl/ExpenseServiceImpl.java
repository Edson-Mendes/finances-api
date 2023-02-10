package br.com.emendes.financesapi.service.impl;

import br.com.emendes.financesapi.controller.dto.ExpenseDto;
import br.com.emendes.financesapi.controller.dto.ValueByCategoryDto;
import br.com.emendes.financesapi.controller.form.ExpenseForm;
import br.com.emendes.financesapi.model.entity.Expense;
import br.com.emendes.financesapi.model.entity.User;
import br.com.emendes.financesapi.repository.ExpenseRepository;
import br.com.emendes.financesapi.service.ExpenseService;
import br.com.emendes.financesapi.util.AuthenticationFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ExpenseServiceImpl implements ExpenseService {

  private final ExpenseRepository expenseRepository;
  private final AuthenticationFacade authenticationFacade;

  @Override
  public ExpenseDto create(ExpenseForm expenseForm) {
    Authentication authentication = authenticationFacade.getAuthentication();
    // Apenas uma precaução caso não tenha usuário logado.
    if (!authentication.isAuthenticated()) {
      throw new RuntimeException("User not found");
    }
    Long userId = ((User) authentication.getPrincipal()).getId();

    Expense expense = expenseForm.convert(userId);
    expenseRepository.save(expense);

    return new ExpenseDto(expense);
  }

  @Override
  public Page<ExpenseDto> readAllByUser(Pageable pageable) {
    Page<Expense> expenses = expenseRepository.findAllByUser(pageable);
    if (expenses.getTotalElements() == 0) {
      throw new NoResultException("O usuário não possui despesas");
    }
    return ExpenseDto.convert(expenses);
  }

  @Override
  public Page<ExpenseDto> readByDescriptionAndUser(String description, Pageable pageable) {
    Page<Expense> expenses = expenseRepository.findByDescriptionAndUser(description, pageable);
    if (expenses.getTotalElements() == 0) {
      throw new NoResultException("O usuário não possui despesas com descrição similar a " + description);
    }
    return ExpenseDto.convert(expenses);
  }

  @Override
  public ExpenseDto readByIdAndUser(Long id) {
    return new ExpenseDto(findByIdAndUser(id));
  }

  @Override
  public Page<ExpenseDto> readByYearAndMonthAndUser(int year, int month, Pageable pageable) {
    Page<Expense> expenses = expenseRepository.findByYearAndMonthAndUser(year, month, pageable);

    if (expenses.getTotalElements() == 0) {
      throw new NoResultException(String.format("Não há despesas para o ano %d e mês %d", year, month));
    }
    return ExpenseDto.convert(expenses);
  }

  @Override
  public ExpenseDto update(Long id, ExpenseForm expenseForm) {
    Expense expenseToBeUpdated = findByIdAndUser(id);

    expenseToBeUpdated.setParams(expenseForm);
    return new ExpenseDto(expenseToBeUpdated);
  }

  @Override
  public void deleteById(Long id) {
    findByIdAndUser(id);
    expenseRepository.deleteById(id);
  }

  @Override
  public List<ValueByCategoryDto> getValuesByCategoryOnMonthAndYearByUser(Integer year, Integer month) {
    return expenseRepository.getValueByCategoryAndMonthAndYearAndUser(year, month);
  }

  private Expense findByIdAndUser(Long id) {
    Optional<Expense> optionalExpense = expenseRepository.findByIdAndUser(id);

    return optionalExpense.orElseThrow(
        () -> new NoResultException(String.format("Nenhuma despesa com id = %d para esse usuário", id)));
  }

}
