package br.com.emendes.financesapi.util.creator;

import java.math.BigDecimal;

import br.com.emendes.financesapi.controller.form.IncomeForm;

public class IncomeFormCreator {

  public static IncomeForm validIncomeForm() {
    String description = "Salário";
    String date = "05/01/2022";
    BigDecimal value = new BigDecimal("2500.00");

    return new IncomeForm(description, date, value);
  }

  public static IncomeForm withBlankDescription(){
    String description = "";
    String date = "05/01/2022";
    BigDecimal value = new BigDecimal("2500.00");

    return new IncomeForm(description, date, value);
  }

  public static IncomeForm withDescription(String description){
    String date = "05/01/2022";
    BigDecimal value = new BigDecimal("2500.00");

    return new IncomeForm(description, date, value);
  }

}
