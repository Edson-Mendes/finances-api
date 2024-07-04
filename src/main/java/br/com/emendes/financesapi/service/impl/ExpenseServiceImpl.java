package br.com.emendes.financesapi.service.impl;

import br.com.emendes.financesapi.dto.request.ExpenseRequest;
import br.com.emendes.financesapi.dto.response.ExpenseResponse;
import br.com.emendes.financesapi.dto.response.ValueByCategoryResponse;
import br.com.emendes.financesapi.exception.EntityNotFoundException;
import br.com.emendes.financesapi.mapper.ExpenseMapper;
import br.com.emendes.financesapi.model.entity.Expense;
import br.com.emendes.financesapi.model.entity.User;
import br.com.emendes.financesapi.repository.ExpenseRepository;
import br.com.emendes.financesapi.service.ExpenseService;
import br.com.emendes.financesapi.util.component.CurrentAuthenticationComponent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ExpenseServiceImpl implements ExpenseService {

  private final ExpenseRepository expenseRepository;
  private final CurrentAuthenticationComponent currentAuthenticationComponent;
  private final ExpenseMapper expenseMapper;

  @Override
  public ExpenseResponse create(ExpenseRequest expenseRequest) {
    log.info("attempt to create new expense.");
    User currentUser = currentAuthenticationComponent.getCurrentUser();

    Expense expense = expenseMapper.toExpense(expenseRequest);
    expense.setUser(currentUser);
    expenseRepository.save(expense);

    return expenseMapper.toExpenseResponse(expense);
  }

  @Override
  public Page<ExpenseResponse> readAllByUser(Pageable pageable) {
    User currentUser = currentAuthenticationComponent.getCurrentUser();
    log.info("attempt to read expenses for user with id: {}.", currentUser.getId());

    Page<Expense> expensePage = expenseRepository.findAllByUser(currentUser, pageable);
    if (expensePage.getTotalElements() == 0) {
      throw new EntityNotFoundException("The user has no expenses");
    }

    return expensePage.map(expenseMapper::toExpenseResponse);
  }

  @Override
  public Page<ExpenseResponse> readByDescriptionAndUser(String description, Pageable pageable) {
    log.info("attempt to read expense by description");

    User currentUser = currentAuthenticationComponent.getCurrentUser();
    Page<Expense> expensePage = expenseRepository.findByDescriptionAndUser(description, currentUser, pageable);
    if (expensePage.getTotalElements() == 0) {
      throw new EntityNotFoundException("The user has no expenses with a description similar to " + description);
    }
    return expensePage.map(expenseMapper::toExpenseResponse);
  }

  @Override
  public ExpenseResponse readByIdAndUser(Long expenseId) {
    log.info("attempt to read expense by id.");
    return expenseMapper.toExpenseResponse(findByIdAndUser(expenseId));
  }

  @Override
  public Page<ExpenseResponse> readByYearAndMonthAndUser(int year, int month, Pageable pageable) {
    log.info("attempt to read expense by year and month.");
    User currentUser = currentAuthenticationComponent.getCurrentUser();
    Page<Expense> expensePage = expenseRepository.findByYearAndMonthAndUser(year, month, currentUser, pageable);

    if (expensePage.getTotalElements() == 0) {
      throw new EntityNotFoundException(String.format("Has no expenses for year %d and month %s", year, Month.of(month)));
    }
    return expensePage.map(expenseMapper::toExpenseResponse);
  }

  @Override
  @Transactional
  public ExpenseResponse update(Long expenseId, ExpenseRequest expenseRequest) {
    log.info("attempt to update expense with id: {}", expenseId);
    Expense expenseToBeUpdated = findByIdAndUser(expenseId);

    expenseMapper.merge(expenseToBeUpdated, expenseRequest);
    return expenseMapper.toExpenseResponse(expenseToBeUpdated);
  }

  @Override
  public void deleteById(Long expenseId) {
    log.info("attempt to delete expense with id: {}", expenseId);

    expenseRepository.delete(findByIdAndUser(expenseId));
  }

  @Override
  public List<ValueByCategoryResponse> getValuesByCategoryOnMonthAndYearByUser(Integer year, Integer month) {
    log.info("attempt to get values by category at year: {} and month: {}", year, month);

    User currentUser = currentAuthenticationComponent.getCurrentUser();
    return expenseRepository.getValueByCategoryAndMonthAndYearAndUser(year, month, currentUser);
  }

  /**
   * Busca Expense por id e user, sendo que o user usado na busca é o usuário logado na requisição atual.
   *
   * @param id identificador da expense a ser buscada.
   * @return Expense encontrada para o dado id e user autenticado.
   * @throws EntityNotFoundException caso não seja encontrada nenhuma expense para o dado id e user autenticado.
   */
  private Expense findByIdAndUser(Long id) {
    log.info("attempt to find expense with id: {}", id);
    User currentUser = currentAuthenticationComponent.getCurrentUser();
    Optional<Expense> optionalExpense = expenseRepository.findByIdAndUser(id, currentUser);

    return optionalExpense.orElseThrow(
        () -> new EntityNotFoundException(String.format("Expense not found with id: %d", id)));
  }

}
