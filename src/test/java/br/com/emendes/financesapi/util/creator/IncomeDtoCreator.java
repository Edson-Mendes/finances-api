package br.com.emendes.financesapi.util.creator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import br.com.emendes.financesapi.dto.response.IncomeResponse;

public class IncomeDtoCreator {

  public static IncomeResponse validIncomeDto() {
    Long id = 54321l;
    String description = "Salário";
    LocalDate date = LocalDate.parse("05/01/2022", DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    BigDecimal value = new BigDecimal("2500.00");

    return new IncomeResponse(id, description, date, value);
  }

}
