package br.com.emendes.financesapi.util.creator;

import java.math.BigDecimal;

import br.com.emendes.financesapi.dto.request.IncomeRequest;

public class IncomeFormCreator {

  public static IncomeRequest validIncomeForm() {
    String description = "Salário";
    String date = "05/01/2022";
    BigDecimal value = new BigDecimal("2500.00");

    return new IncomeRequest(description, date, value);
  }

  public static IncomeRequest withBlankDescription(){
    String description = "";
    String date = "05/01/2022";
    BigDecimal value = new BigDecimal("2500.00");

    return new IncomeRequest(description, date, value);
  }

  public static IncomeRequest withDescription(String description){
    String date = "05/01/2022";
    BigDecimal value = new BigDecimal("2500.00");

    return new IncomeRequest(description, date, value);
  }

}
