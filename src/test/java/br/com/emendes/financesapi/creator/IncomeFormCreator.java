package br.com.emendes.financesapi.creator;

import java.math.BigDecimal;

import br.com.emendes.financesapi.controller.form.IncomeForm;

public class IncomeFormCreator {

  public static IncomeForm validIncomeForm() {
    String description = "Sálario";
    String date = "05/01/2022";
    BigDecimal value = new BigDecimal("2500.00");

    return new IncomeForm(description, date, value);
  }

}
