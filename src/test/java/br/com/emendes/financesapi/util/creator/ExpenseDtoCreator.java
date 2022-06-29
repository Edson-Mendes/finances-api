package br.com.emendes.financesapi.util.creator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import br.com.emendes.financesapi.controller.dto.ExpenseDto;
import br.com.emendes.financesapi.model.enumerator.Category;

public class ExpenseDtoCreator {

  public static ExpenseDto validExpenseDto() {
    Long id = 12345l;
    String description = "Gasolina";
    LocalDate date = LocalDate.parse("17/01/2022", DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    BigDecimal value = new BigDecimal("250.00");
    Category category = Category.TRANSPORTE;
    return new ExpenseDto(id, description, date, value, category);
  }

}
