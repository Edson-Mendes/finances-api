package br.com.emendes.financesapi.unit.form;

import java.math.BigDecimal;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.emendes.financesapi.dto.request.ExpenseRequest;
import br.com.emendes.financesapi.util.creator.ExpenseFormCreator;
import br.com.emendes.financesapi.model.Category;

@DisplayName("Tests for ExpenseForm")
public class ExpenseRequestTests {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  @DisplayName("Must return empty violations when ExpenseForm is valid")
  void mustReturnEmptyViolations_WhenExpenseFormIsValid() {
    ExpenseRequest validExpenseRequest = ExpenseFormCreator.validExpenseForm();

    Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(validExpenseRequest);

    Assertions.assertThat(violations).isEmpty();
  }

  @Test
  @DisplayName("Must return empty violations when category is null")
  void mustReturnEmptyViolations_WhenCategoryIsNull() {
    String description = "Mercado";
    String date = "23/01/2022";
    BigDecimal value = new BigDecimal("250.00");
    Category category = null;

    ExpenseRequest invalidExpenseRequest = new ExpenseRequest(description, date, value, category);

    Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(invalidExpenseRequest);

    Assertions.assertThat(violations).isEmpty();
  }

  @Test
  @DisplayName("Must return violations when date is invalid")
  void mustReturnViolations_WhenDateIsInvalid() {
    String description = "Mercado";
    String invalidDate = "23-01-2022";
    BigDecimal value = new BigDecimal("250.00");
    Category category = Category.ALIMENTACAO;

    ExpenseRequest invalidExpenseRequest = new ExpenseRequest(description, invalidDate, value, category);

    Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(invalidExpenseRequest);

    Assertions.assertThat(violations)
        .isNotEmpty()
        .hasSize(1);
    Assertions.assertThat(violations.stream().findAny().get().getMessage())
        .isEqualTo("A data informada é inválida!");
  }

  @Test
  @DisplayName("Must return violations when value is invalid")
  void mustReturnViolations_WhenValueIsInvalid() {
    String description = "Mercado";
    String date = "23/01/2022";
    BigDecimal invalidValue = new BigDecimal("2500000.00");
    Category category = Category.ALIMENTACAO;

    ExpenseRequest invalidExpenseRequest = new ExpenseRequest(description, date, invalidValue, category);

    Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(invalidExpenseRequest);

    Assertions.assertThat(violations)
        .isNotEmpty()
        .hasSize(1);
  }

  @Test
  @DisplayName("Must return violations when value is negative")
  void mustReturnViolations_WhenValueIsNegative() {
    String description = "Mercado";
    String date = "23/01/2022";
    BigDecimal invalidValue = new BigDecimal("-250.00");
    Category category = Category.ALIMENTACAO;

    ExpenseRequest invalidExpenseRequest = new ExpenseRequest(description, date, invalidValue, category);

    Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(invalidExpenseRequest);

    Assertions.assertThat(violations)
        .isNotEmpty()
        .hasSize(1);
  }

  @Test
  @DisplayName("Must return violations when description is null")
  void mustReturnViolations_WhenDescriptionIsNull() {
    String description = null;
    String date = "23/01/2022";
    BigDecimal value = new BigDecimal("250.00");
    Category category = Category.ALIMENTACAO;

    ExpenseRequest invalidExpenseRequest = new ExpenseRequest(description, date, value, category);

    Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(invalidExpenseRequest);

    Assertions.assertThat(violations)
        .isNotEmpty()
        .hasSize(1);
  }

  @Test
  @DisplayName("Must return violations when date is null")
  void mustReturnViolations_WhenDateIsNull() {
    String description = "Mercado";
    String date = null;
    BigDecimal value = new BigDecimal("250.00");
    Category category = Category.ALIMENTACAO;

    ExpenseRequest invalidExpenseRequest = new ExpenseRequest(description, date, value, category);

    Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(invalidExpenseRequest);

    Assertions.assertThat(violations)
        .isNotEmpty()
        .hasSize(2);
  }

  @Test
  @DisplayName("Must return violations when value is null")
  void mustReturnViolations_WhenValueIsNull() {
    String description = "Mercado";
    String date = "23/01/2022";
    BigDecimal value = null;
    Category category = Category.ALIMENTACAO;

    ExpenseRequest invalidExpenseRequest = new ExpenseRequest(description, date, value, category);

    Set<ConstraintViolation<ExpenseRequest>> violations = validator.validate(invalidExpenseRequest);

    Assertions.assertThat(violations)
        .isNotEmpty()
        .hasSize(1);
  }

}
