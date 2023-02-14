package br.com.emendes.financesapi.util.creator;

import java.math.BigDecimal;

import br.com.emendes.financesapi.dto.request.ExpenseRequest;
import br.com.emendes.financesapi.model.Category;

public class ExpenseFormCreator {

  public static ExpenseRequest validExpenseForm() {
    String description = "Gasolina";
    String date = "17/01/2022";
    BigDecimal value = new BigDecimal("250.00");
    Category category = Category.TRANSPORTE;

    return new ExpenseRequest(description, date, value, category);
  }

  public static ExpenseRequest withBlankDescription() {
    String description = "";
    String date = "17/01/2022";
    BigDecimal value = new BigDecimal("250.00");
    Category category = Category.TRANSPORTE;

    return new ExpenseRequest(description, date, value, category);
  }

  public static ExpenseRequest withDescription(String description) {
    BigDecimal value = new BigDecimal("250.00");
    String date = "17/01/2022";
    Category category = Category.TRANSPORTE;

    return new ExpenseRequest(description, date, value, category);
  }
}
