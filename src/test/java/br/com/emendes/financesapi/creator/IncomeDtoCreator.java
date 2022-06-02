package br.com.emendes.financesapi.creator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import br.com.emendes.financesapi.controller.dto.IncomeDto;

public class IncomeDtoCreator {

  public static IncomeDto validIncomeDto() {
    Long id = 54321l;
    String description = "Sálario";
    LocalDate date = LocalDate.parse("05/01/2022", DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    BigDecimal value = new BigDecimal("2500.00");

    return new IncomeDto(id, description, date, value);
  }

}
