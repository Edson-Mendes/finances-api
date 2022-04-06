package br.com.emendes.financesapi.controller.form;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import br.com.emendes.financesapi.config.validation.annotation.DateValidation;
import br.com.emendes.financesapi.model.Expense;
import br.com.emendes.financesapi.model.User;
import br.com.emendes.financesapi.model.enumerator.Category;
import br.com.emendes.financesapi.util.Formatter;
import io.swagger.v3.oas.annotations.media.Schema;

public class ExpenseForm {

  @Schema(example = "Aluguel")
  @NotBlank
  private String description;

  @Schema(pattern = "dd/MM/yyyy", type = "string", example = "08/01/2022")
  @NotNull
  @DateValidation
  private String date;

  @Schema(example = "1200.00")
  @NotNull
  @Positive
  @Digits(integer = 6, fraction = 2)
  private BigDecimal value;

  @Schema(example = "MORADIA")
  private Category category;

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
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

  public Expense convert(Long userId) {
    LocalDate date = LocalDate.parse(this.date, Formatter.dateFormatter);
    if (category == null) {
      category = Category.OUTRAS;
    }
    User user = new User(userId);
    Expense expense = new Expense(description, value, date, category, user);
    return expense;
  }

}
