package br.com.emendes.financesapi.controller.form;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import br.com.emendes.financesapi.config.validation.annotation.DateValidation;
import br.com.emendes.financesapi.model.Expense;
import br.com.emendes.financesapi.model.enumerator.Category;
import br.com.emendes.financesapi.repository.ExpenseRepository;
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

  public Expense convert(ExpenseRepository expenseRepository, Long userId) {
    alreadyExist(expenseRepository, userId);
    LocalDate date = LocalDate.parse(this.date, Formatter.dateFormatter);
    if (category == null) {
      category = Category.OUTRAS;
    }
    Expense expense = new Expense(description, value, date, category);
    return expense;
  }

  /**
   * Verifica se o usuário já possui uma despesa com a mesma descrição no mês e
   * ano da
   * respectiva despesa.
   * 
   * @param expenseRepository
   * @param userId
   * @return false, se não existir uma despesa com a mesma descrição em um mesmo
   *         mês e ano.
   * @throws ResponseStatusException se existir despesa.
   */
  private boolean alreadyExist(ExpenseRepository expenseRepository, Long userId) {
    LocalDate date = LocalDate.parse(this.date, Formatter.dateFormatter);
    Optional<Expense> optional = expenseRepository.findByDescriptionAndMonthAndYearAndUserId(
        description,
        date.getMonthValue(),
        date.getYear(),
        userId);
    if (optional.isPresent()) {
      String message = "Uma despesa com essa descrição já existe em " + date.getMonth().name().toLowerCase() + " "
          + date.getYear();
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, message, null);
    }
    return false;
  }

  /**
   * Verifica se o usuário já possui uma despesa com a mesma descrição no mês e
   * ano da
   * respectiva despesa.
   * e com id diferente do mesmo.
   * 
   * @param expenseRepository
   * @param id
   * @param userId
   * @return false, se não existir uma despesa com a mesma descrição em um mesmo
   *         mês e ano.
   * @throws ResponseStatusException se existir despesa.
   */
  public boolean alreadyExist(ExpenseRepository expenseRepository, Long id, Long userId) {
    LocalDate date = LocalDate.parse(this.date, Formatter.dateFormatter);
    Optional<Expense> optional = expenseRepository.findByDescriptionAndMonthAndYearAndUserIdAndNotId(
        description,
        date.getMonthValue(),
        date.getYear(),
        userId,
        id);
    if (optional.isPresent()) {
      String message = "Descrição de despesa duplicada para " + date.getMonth().name().toLowerCase() + " "
          + date.getYear();
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, message, null);
    }
    return false;
  }

}
