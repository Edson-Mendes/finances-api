package br.com.emendes.financesapi.service.impl;

import br.com.emendes.financesapi.controller.dto.ValueByCategoryDto;
import br.com.emendes.financesapi.dto.request.ExpenseRequest;
import br.com.emendes.financesapi.dto.response.ExpenseResponse;
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
  public ExpenseResponse create(ExpenseRequest expenseRequest) {
    Authentication authentication = authenticationFacade.getAuthentication();
    // Apenas uma precaução caso não tenha usuário logado.
    if (!authentication.isAuthenticated()) {
      throw new RuntimeException("User not found");
    }
    Long userId = ((User) authentication.getPrincipal()).getId();

    Expense expense = expenseRequest.convert(userId);
    expenseRepository.save(expense);

    return new ExpenseResponse(expense);
  }

  @Override
  public Page<ExpenseResponse> readAllByUser(Pageable pageable) {
    Page<Expense> expenses = expenseRepository.findAllByUser(pageable);
    if (expenses.getTotalElements() == 0) {
      // TODO: Criar uma exception para ser lançada aqui!
      throw new NoResultException("The user has no expenses");
    }
    return ExpenseResponse.convert(expenses);
  }

  @Override
  public Page<ExpenseResponse> readByDescriptionAndUser(String description, Pageable pageable) {
    Page<Expense> expenses = expenseRepository.findByDescriptionAndUser(description, pageable);
    if (expenses.getTotalElements() == 0) {
      throw new NoResultException("The user has no expenses with a description similar to " + description);
    }
    return ExpenseResponse.convert(expenses);
  }

  @Override
  public ExpenseResponse readByIdAndUser(Long id) {
    return new ExpenseResponse(findByIdAndUser(id));
  }

  @Override
  public Page<ExpenseResponse> readByYearAndMonthAndUser(int year, int month, Pageable pageable) {
    Page<Expense> expenses = expenseRepository.findByYearAndMonthAndUser(year, month, pageable);

    if (expenses.getTotalElements() == 0) {
      throw new NoResultException(String.format("Has no expenses for year %d and month %d", year, month));
    }
    return ExpenseResponse.convert(expenses);
  }

  @Override
  public ExpenseResponse update(Long id, ExpenseRequest expenseRequest) {
    Expense expenseToBeUpdated = findByIdAndUser(id);

    expenseToBeUpdated.setParams(expenseRequest);
    return new ExpenseResponse(expenseToBeUpdated);
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
        () -> new NoResultException("Expense not found"));
  }

}
