package br.com.emendes.financesapi.mapper;

import br.com.emendes.financesapi.dto.request.ExpenseRequest;
import br.com.emendes.financesapi.dto.response.ExpenseResponse;
import br.com.emendes.financesapi.model.entity.Expense;

/**
 * Interface component com as abstrações para mapeamento do recurso Expense.
 */
public interface ExpenseMapper {

  /**
   * Mapeia um objeto {@link ExpenseRequest} para {@link Expense}.
   *
   * @param expenseRequest objeto a ser mapeado.
   * @return Objeto Expense.
   * @throws IllegalArgumentException caso expenseRequest seja null.
   */
  Expense toExpense(ExpenseRequest expenseRequest);

  /**
   * Mapeia um objeto {@link Expense} para {@link ExpenseResponse}.
   *
   * @param expense objeto a ser mapeado.
   * @return Objeto ExpenseResponse.
   * @throws IllegalArgumentException caso expense seja null.
   */
  ExpenseResponse toExpenseResponse(Expense expense);

  /**
   * Mescla os dados de expense com os dados de expenseRequest.
   *
   * @param expense        objeto que receberá novos dados.
   * @param expenseRequest objeto fonte dos novos dados.
   * @throws IllegalArgumentException caso expense ou expenseRequest sejam nulos.
   */
  void merge(Expense expense, ExpenseRequest expenseRequest);

}
