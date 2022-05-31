package br.com.emendes.financesapi.creator;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.com.emendes.financesapi.model.Expense;
import br.com.emendes.financesapi.model.User;
import br.com.emendes.financesapi.model.enumerator.Category;

public class ExpenseCreator {
  public static Expense validExpenseWithoutId() {
    String description = "Gasolina";
    BigDecimal value = new BigDecimal("250.00");
    LocalDate date = LocalDate.parse("2022-01-17");
    Category category = Category.TRANSPORTE;
    User user = new User(2l);

    return new Expense(description, value, date, category, user);
  }

  public static Expense validExpense() {
    String description = "Feira de alimentos";
    BigDecimal value = new BigDecimal("75.00");
    LocalDate date = LocalDate.parse("2022-01-23");
    Category category = Category.ALIMENTACAO;
    User user = new User(2l);

    return new Expense(description, value, date, category, user);
  }

  public static Expense validExpenseWithUser(User user) {
    String description = "Gasolina";
    BigDecimal value = new BigDecimal("250.00");
    LocalDate date = LocalDate.parse("2022-01-17");
    Category category = Category.TRANSPORTE;

    return new Expense(description, value, date, category, user);
  }
}
