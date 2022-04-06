package br.com.emendes.financesapi.controller.form;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import br.com.emendes.financesapi.config.validation.annotation.DateValidation;
import br.com.emendes.financesapi.model.Income;
import br.com.emendes.financesapi.model.User;
import br.com.emendes.financesapi.util.Formatter;
import io.swagger.v3.oas.annotations.media.Schema;

public class IncomeForm {

  @Schema(example = "Salário")
  @NotBlank
  private String description;

  @Schema(pattern = "dd/MM/yyyy", type = "string", example = "05/01/2022")
  @NotNull
  @DateValidation
  private String date;

  @Schema(example = "3500.00")
  @NotNull
  @Positive
  @Digits(integer = 6, fraction = 2)
  private BigDecimal value;

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public BigDecimal getValue() {
    return value;
  }

  public void setValue(BigDecimal value) {
    this.value = value;
  }

  public Income convert(Long userId) {
    LocalDate date = LocalDate.parse(this.date, Formatter.dateFormatter);
    User user = new User(userId);
    Income income = new Income(description, value, date, user);
    return income;
  }

}
