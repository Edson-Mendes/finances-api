package br.com.emendes.financesapi.util.creator;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.com.emendes.financesapi.model.entity.Income;
import br.com.emendes.financesapi.model.entity.User;

public class IncomeCreator {

  public static Income validIncomeWithoutId() {
    String description = "Sálario";
    BigDecimal value = new BigDecimal("2500.00");
    LocalDate date = LocalDate.parse("2022-01-05");

    return new Income(description, value, date, null);
  }

  public static Income validIncome() {
    String description = "Venda Livros";
    BigDecimal value = new BigDecimal("150.00");
    LocalDate date = LocalDate.parse("2022-01-12");

    return new Income(description, value, date, null);
  }

  public static Income validIncomeWithUser(User user) {
    String description = "Sálario";
    BigDecimal value = new BigDecimal("2500.00");
    LocalDate date = LocalDate.parse("2022-01-05");

    return new Income(description, value, date, user);
  }

  public static Income incomeWithAllArgs() {
    Long id = 1000L;
    String description = "Sálario";
    BigDecimal value = new BigDecimal("2500.00");
    LocalDate date = LocalDate.parse("2022-01-05");
    User user = new User(2l);

    Income income = new Income(description, value, date, user);
    income.setId(id);
    return income;
  }

  public static Income withDescription(String description){
    BigDecimal value = new BigDecimal("2500.00");
    LocalDate date = LocalDate.parse("2022-01-05");
    User user = new User(2L);

    return new Income(description, value, date, user);
  }

  public static Income withDescriptionAndValue(String description, BigDecimal value){
    LocalDate date = LocalDate.parse("2022-01-05");
    User user = new User(2L);

    return new Income(description, value, date, user);
  }

}
