package br.com.emendes.financesapi.util.creator;

import java.math.BigDecimal;

import br.com.emendes.financesapi.controller.form.ExpenseForm;
import br.com.emendes.financesapi.model.Category;

public class ExpenseFormCreator {

  public static ExpenseForm validExpenseForm() {
    String description = "Gasolina";
    String date = "17/01/2022";
    BigDecimal value = new BigDecimal("250.00");
    Category category = Category.TRANSPORTE;

    return new ExpenseForm(description, date, value, category);
  }

  public static ExpenseForm withBlankDescription() {
    String description = "";
    String date = "17/01/2022";
    BigDecimal value = new BigDecimal("250.00");
    Category category = Category.TRANSPORTE;

    return new ExpenseForm(description, date, value, category);
  }

  public static ExpenseForm withDescription(String description) {
    BigDecimal value = new BigDecimal("250.00");
    String date = "17/01/2022";
    Category category = Category.TRANSPORTE;

    return new ExpenseForm(description, date, value, category);
  }
}
