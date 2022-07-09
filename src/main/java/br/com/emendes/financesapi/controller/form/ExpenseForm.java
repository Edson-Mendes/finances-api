package br.com.emendes.financesapi.controller.form;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import br.com.emendes.financesapi.model.Expense;
import br.com.emendes.financesapi.model.User;
import br.com.emendes.financesapi.model.enumerator.Category;
import br.com.emendes.financesapi.util.Formatter;
import br.com.emendes.financesapi.validation.annotation.DateValidation;
import io.swagger.v3.oas.annotations.media.Schema;

public class ExpenseForm {

  @Schema(example = "Mercado")
  @NotBlank
  private String description;

  @Schema(pattern = "dd/MM/yyyy", type = "string", example = "17/01/2022")
  @NotNull
  @DateValidation
  private String date;

  @Schema(example = "271.94")
  @NotNull
  @Positive
  @Digits(integer = 6, fraction = 2)
  private BigDecimal value;

//  TODO: Fazer um bean validation para validar a categoria
  @Schema(example = "ALIMENTACAO")
  private Category category;

  public ExpenseForm() {
  }

  public ExpenseForm(String description, String date, BigDecimal value, Category category) {
    this.description = description;
    this.date = date;
    this.value = value;
    this.category = category;
  }

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
    LocalDate date = parseDateToLocalDate();
    if (category == null) {
      category = Category.OUTRAS;
    }
    User user = new User(userId);
    Expense expense = new Expense(description, value, date, category, user);
    return expense;
  }

  public LocalDate parseDateToLocalDate() {
    return LocalDate.parse(this.date, Formatter.dateFormatter);
  }

}
