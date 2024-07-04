package br.com.emendes.financesapi.util.faker;

import br.com.emendes.financesapi.dto.response.ExpenseResponse;
import br.com.emendes.financesapi.dto.response.ValueByCategoryResponse;
import br.com.emendes.financesapi.model.Category;
import br.com.emendes.financesapi.model.entity.Expense;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static br.com.emendes.financesapi.util.faker.UserFaker.user;

/**
 * Classe com objetos relacionados a {@link Expense} para serem usados em testes automatizados.
 */
public class ExpenseFaker {

  public static final long EXPENSE_ID = 100_000L;
  public static final String EXPENSE_DESCRIPTION = "Aluguel xpto";
  public static final BigDecimal EXPENSE_VALUE = new BigDecimal("1500.00");
  public static final LocalDate EXPENSE_DATE = LocalDate.parse("2023-02-05");
  public static final String UPDATED_EXPENSE_DESCRIPTION = "Aluguel xpto updated";
  public static final BigDecimal UPDATED_EXPENSE_VALUE = new BigDecimal("1750.00");
  public static final LocalDate UPDATED_EXPENSE_DATE = LocalDate.parse("2023-02-08");

  /**
   * Retorna uma {@link Expense} com todos os campos.
   */
  public static Expense expense() {
    return Expense.builder()
        .id(EXPENSE_ID)
        .description(EXPENSE_DESCRIPTION)
        .category(Category.MORADIA)
        .value(EXPENSE_VALUE)
        .date(EXPENSE_DATE)
        .user(user())
        .build();
  }

  /**
   * Retorna um {@code List<Expense>} com um elemento.
   */
  public static List<Expense> expenseList() {
    return List.of(expense());
  }

  /**
   * Retorna um {@code Optional<Expense>} não vazio.
   */
  public static Optional<Expense> expenseOptional() {
    return Optional.of(expense());
  }

  /**
   * Retorna um {@link ExpenseResponse} com todos os campos.
   */
  public static ExpenseResponse expenseResponse() {
    return ExpenseResponse.builder()
        .id(EXPENSE_ID)
        .description(EXPENSE_DESCRIPTION)
        .category(Category.MORADIA)
        .value(EXPENSE_VALUE)
        .date(EXPENSE_DATE)
        .build();
  }

  /**
   * Retorna um {@link ValueByCategoryResponse} para um dado value e category.
   *
   * @param category categoria relacionada com o objeto.
   * @param value    valor total das despesas com essa categoria.
   * @return Objeto {@link ValueByCategoryResponse}.
   */
  public static ValueByCategoryResponse valueByCategory(Category category, String value) {
    return ValueByCategoryResponse.builder()
        .category(category)
        .value(new BigDecimal(value))
        .build();
  }

  /**
   * Retorna um {@link ExpenseResponse} com os campos atualizados.
   */
  public static ExpenseResponse updatedExpenseResponse() {
    return ExpenseResponse.builder()
        .id(EXPENSE_ID)
        .description(UPDATED_EXPENSE_DESCRIPTION)
        .value(UPDATED_EXPENSE_VALUE)
        .date(UPDATED_EXPENSE_DATE)
        .build();
  }

}
