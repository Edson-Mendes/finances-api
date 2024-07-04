package br.com.emendes.financesapi.mapper.impl;

import br.com.emendes.financesapi.dto.request.ExpenseRequest;
import br.com.emendes.financesapi.dto.response.ExpenseResponse;
import br.com.emendes.financesapi.mapper.ExpenseMapper;
import br.com.emendes.financesapi.model.Category;
import br.com.emendes.financesapi.model.entity.Expense;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.LocalDate;

/**
 * Implementação de {@link ExpenseMapper}.
 */
@Component
public class ExpenseMapperImpl implements ExpenseMapper {

  @Override
  public Expense toExpense(ExpenseRequest expenseRequest) {
    Assert.notNull(expenseRequest, "expenseRequest must not be null.");

    return Expense.builder()
        .description(expenseRequest.getDescription())
        .value(expenseRequest.getValue())
        .category(toCategory(expenseRequest.getCategory()))
        .date(LocalDate.parse(expenseRequest.getDate()))
        .build();
  }

  @Override
  public ExpenseResponse toExpenseResponse(Expense expense) {
    Assert.notNull(expense, "expense must not be null.");

    return ExpenseResponse.builder()
        .id(expense.getId())
        .description(expense.getDescription())
        .value(expense.getValue())
        .category(expense.getCategory())
        .date(expense.getDate())
        .build();
  }

  @Override
  public void merge(Expense expense, ExpenseRequest expenseRequest) {
    Assert.notNull(expense, "expense must not be null.");
    Assert.notNull(expenseRequest, "expenseRequest must not be null.");

    expense.setDescription(expenseRequest.getDescription());
    expense.setValue(expenseRequest.getValue());
    expense.setDate(LocalDate.parse(expenseRequest.getDate()));
    if (expenseRequest.getCategory() != null) {
      expense.setCategory(Category.valueOf(expenseRequest.getCategory()));
    }
  }

  /**
   * Converte uma String em um {@link Category}.
   *
   * @param categoryAsString category como String.
   * @return Category que corresponde a categoryAsString, caso categoryAsString seja null,
   * retorna {@code Category.OUTRAS}
   */
  private Category toCategory(String categoryAsString) {
    if (categoryAsString == null) return Category.OUTRAS;
    return Category.valueOf(categoryAsString);
  }

}
