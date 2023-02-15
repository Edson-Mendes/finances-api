package br.com.emendes.financesapi.unit.dto.request;

import br.com.emendes.financesapi.dto.request.ExpenseRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@DisplayName("Tests for ExpenseRequest")
class ExpenseRequestTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  private final String VALID_DESCRIPTION = "Aluguel";
  private final String VALID_DATE = "2023-02-05";
  private final BigDecimal VALID_VALUE = new BigDecimal("1500.00");
  private final String VALID_CATEGORY = "MORADIA";

  @Nested
  @DisplayName("Tests for description validation")
  class DescriptionValidation {

    private final String DESCRIPTION_PROPERTY_NAME = "description";
    private final ExpenseRequest.ExpenseRequestBuilder expenseRequestBuilder = ExpenseRequest.builder()
        .date(VALID_DATE)
        .value(VALID_VALUE)
        .category(VALID_CATEGORY);

    @Test
    @DisplayName("Validate description must not return violations when description is valid")
    void validateDescription_MustNotReturnViolations_WhenDescriptionIsValid() {
      ExpenseRequest expenseRequest = expenseRequestBuilder
          .description("Aluguel")
          .build();

      Set<ConstraintViolation<ExpenseRequest>> actualViolations = validator
          .validateProperty(expenseRequest, DESCRIPTION_PROPERTY_NAME);

      Assertions.assertThat(actualViolations).isEmpty();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    @DisplayName("Validate description must return violations when description is blank")
    void validateDescription_MustReturnViolations_WhenDescriptionIsBlank(String blankDescription) {
      ExpenseRequest expenseRequest = expenseRequestBuilder
          .description(blankDescription)
          .build();

      Set<ConstraintViolation<ExpenseRequest>> actualViolations = validator
          .validateProperty(expenseRequest, DESCRIPTION_PROPERTY_NAME);

      List<String> actualMessages = actualViolations.stream().map(ConstraintViolation::getMessage).toList();

      Assertions.assertThat(actualViolations).isNotEmpty();
      Assertions.assertThat(actualMessages).contains("description must not be null or blank");
    }

    @Test
    @DisplayName("Validate description must return violations when description contains more than 255 characters")
    void validateDescription_MustReturnViolations_WhenDescriptionContainsMoreThan255Characters() {
      String descriptionWithMoreThan255Characters = "AluguelAluguelAluguelAluguelAluguelAluguel"+
          "AluguelAluguelAluguelAluguelAluguelAluguelAluguelAluguelAluguelAluguel" +
          "AluguelAluguelAluguelAluguelAluguelAluguelAluguelAluguelAluguelAluguel" +
          "AluguelAluguelAluguelAluguelAluguelAluguelAluguelAluguelAluguelAluguelAluguel";

      ExpenseRequest expenseRequest = expenseRequestBuilder
          .description(descriptionWithMoreThan255Characters)
          .build();

      Set<ConstraintViolation<ExpenseRequest>> actualViolations = validator
          .validateProperty(expenseRequest, DESCRIPTION_PROPERTY_NAME);
      List<String> actualMessages = actualViolations.stream().map(ConstraintViolation::getMessage).toList();

      Assertions.assertThat(descriptionWithMoreThan255Characters.length()).isGreaterThan(255);
      Assertions.assertThat(actualViolations).isNotEmpty();
      Assertions.assertThat(actualMessages).contains("description must contain max 255 characters");
    }

  }

  @Nested
  @DisplayName("Tests for date validation")
  class DateValidation {

    private final String DATE_PROPERTY_NAME = "date";

    private final ExpenseRequest.ExpenseRequestBuilder expenseRequestBuilder = ExpenseRequest.builder()
        .description(VALID_DESCRIPTION)
        .value(VALID_VALUE)
        .category(VALID_CATEGORY);

    @Test
    @DisplayName("Validate date must not return violations when date is valid")
    void validateDate_MustNotReturnViolations_WhenDateIsValid() {
      ExpenseRequest expenseRequest = expenseRequestBuilder
          .date("2023-02-05")
          .build();

      Set<ConstraintViolation<ExpenseRequest>> actualViolations = validator
          .validateProperty(expenseRequest, DATE_PROPERTY_NAME);

      Assertions.assertThat(actualViolations).isEmpty();
    }

    @Test
    @DisplayName("Validate date must return Violations when date is null")
    void validateDate_MustReturnViolations_WhenDateIsNull() {
      ExpenseRequest expenseRequest = expenseRequestBuilder
          .date(null)
          .build();

      Set<ConstraintViolation<ExpenseRequest>> actualViolations = validator
          .validateProperty(expenseRequest, DATE_PROPERTY_NAME);
      List<String> actualMessages = actualViolations.stream().map(ConstraintViolation::getMessage).toList();

      Assertions.assertThat(actualViolations).isNotEmpty();
      Assertions.assertThat(actualMessages).contains("date must not be null");
    }

    @ParameterizedTest
    @ValueSource(strings = {"05/02/2023", "05-02-2023", "20230205", "2023-02", "2023-02-"})
    @DisplayName("Validate date must return violations when date is invalid")
    void validateDate_MustReturnViolations_WhenDateIsInvalid(String invalidDate) {
      ExpenseRequest expenseRequest = expenseRequestBuilder
          .date(invalidDate)
          .build();

      Set<ConstraintViolation<ExpenseRequest>> actualViolations = validator
          .validateProperty(expenseRequest, DATE_PROPERTY_NAME);
      List<String> actualMessages = actualViolations.stream().map(ConstraintViolation::getMessage).toList();

      Assertions.assertThat(actualViolations).isNotEmpty();
      Assertions.assertThat(actualMessages).contains("invalid date");
    }

  }

  @Nested
  @DisplayName("Tests for value validation")
  class ValueValidation {

    private final String VALUE_PROPERTY_NAME = "value";

    private final ExpenseRequest.ExpenseRequestBuilder expenseRequestBuilder = ExpenseRequest.builder()
        .description(VALID_DESCRIPTION)
        .date(VALID_DATE)
        .category(VALID_CATEGORY);

    @ParameterizedTest
    @ValueSource(strings = {"1500.99", "1500", "150000.99", "0.99"})
    @DisplayName("Validate value must not return violations when value is valid")
    void validateValue_MustNotReturnViolations_WhenValueIsValid(String validValue) {
      ExpenseRequest expenseRequest = expenseRequestBuilder
          .value(new BigDecimal(validValue))
          .build();

      Set<ConstraintViolation<ExpenseRequest>> actualViolations = validator
          .validateProperty(expenseRequest, VALUE_PROPERTY_NAME);

      Assertions.assertThat(actualViolations).isEmpty();
    }

    @Test
    @DisplayName("Validate value must return Violations value is null")
    void validateValue_MustReturnViolations_WhenValueIsNull() {
      ExpenseRequest expenseRequest = expenseRequestBuilder
          .value(null)
          .build();

      Set<ConstraintViolation<ExpenseRequest>> actualViolations = validator
          .validateProperty(expenseRequest, VALUE_PROPERTY_NAME);
      List<String> actualMessages = actualViolations.stream().map(ConstraintViolation::getMessage).toList();

      Assertions.assertThat(actualViolations).isNotEmpty();
      Assertions.assertThat(actualMessages).contains("value must not be null");
    }

    @Test
    @DisplayName("Validate value must return Violations when value is negative")
    void validateValue_MustReturnViolations_WhenValueIsNegative() {
      ExpenseRequest expenseRequest = expenseRequestBuilder
          .value(new BigDecimal("-1500.00"))
          .build();

      Set<ConstraintViolation<ExpenseRequest>> actualViolations = validator
          .validateProperty(expenseRequest, VALUE_PROPERTY_NAME);
      List<String> actualMessages = actualViolations.stream().map(ConstraintViolation::getMessage).toList();

      Assertions.assertThat(actualViolations).isNotEmpty();
      Assertions.assertThat(actualMessages).contains("value must be positive");
    }

    @Test
    @DisplayName("Validate value must return Violations when value's integer part has more than 6 digits")
    void validateValue_MustReturnViolations_WhenValuesIntegerPartHasMoreThan6Digits() {
      ExpenseRequest expenseRequest = expenseRequestBuilder
          .value(new BigDecimal("1500000.00"))
          .build();

      Set<ConstraintViolation<ExpenseRequest>> actualViolations = validator
          .validateProperty(expenseRequest, VALUE_PROPERTY_NAME);
      List<String> actualMessages = actualViolations.stream().map(ConstraintViolation::getMessage).toList();

      Assertions.assertThat(actualViolations).isNotEmpty();
      Assertions.assertThat(actualMessages)
          .contains("Integer part must be max 6 digits and fraction part must be max 2 digits");
    }

    @Test
    @DisplayName("Validate value must return Violations when value's fraction part has more than 2 digits")
    void validateValue_MustReturnViolations_WhenValuesFractionPartHasMoreThan6Digits() {
      ExpenseRequest expenseRequest = expenseRequestBuilder
          .value(new BigDecimal("1500.999"))
          .build();

      Set<ConstraintViolation<ExpenseRequest>> actualViolations = validator
          .validateProperty(expenseRequest, VALUE_PROPERTY_NAME);
      List<String> actualMessages = actualViolations.stream().map(ConstraintViolation::getMessage).toList();

      Assertions.assertThat(actualViolations).isNotEmpty();
      Assertions.assertThat(actualMessages)
          .contains("Integer part must be max 6 digits and fraction part must be max 2 digits");
    }

  }

  @Nested
  @DisplayName("Tests for category validation")
  class CategoryValidation {

    private final String CATEGORY_PROPERTY_NAME = "category";
    private final ExpenseRequest.ExpenseRequestBuilder expenseRequestBuilder = ExpenseRequest.builder()
        .date(VALID_DATE)
        .value(VALID_VALUE)
        .description(VALID_DESCRIPTION);

    @Test
    @DisplayName("Validate category must not return violations when category is valid")
    void validateCategory_MustNotReturnViolations_WhenCategoryIsValid() {
      ExpenseRequest expenseRequest = expenseRequestBuilder
          .category("MORADIA")
          .build();

      Set<ConstraintViolation<ExpenseRequest>> actualViolations = validator
          .validateProperty(expenseRequest, CATEGORY_PROPERTY_NAME);

      Assertions.assertThat(actualViolations).isEmpty();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    @DisplayName("Validate category must return violations when category is blank")
    void validateCategory_MustReturnViolations_WhenCategoryIsBlank(String blankCategory) {
      ExpenseRequest expenseRequest = expenseRequestBuilder
          .category(blankCategory)
          .build();

      Set<ConstraintViolation<ExpenseRequest>> actualViolations = validator
          .validateProperty(expenseRequest, CATEGORY_PROPERTY_NAME);

      List<String> actualMessages = actualViolations.stream().map(ConstraintViolation::getMessage).toList();

      Assertions.assertThat(actualViolations).isNotEmpty();
      Assertions.assertThat(actualMessages).contains("category must not be null or blank");
    }

    @ParameterizedTest
    @ValueSource(strings = {"alimentacao", "xalala", "OTHER"})
    @DisplayName("Validate category must return violations when category is invalid")
    void validateCategory_MustReturnViolations_WhenCategoryIsInvalid(String invalidCategory) {
      ExpenseRequest expenseRequest = expenseRequestBuilder
          .category(invalidCategory)
          .build();

      Set<ConstraintViolation<ExpenseRequest>> actualViolations = validator
          .validateProperty(expenseRequest, CATEGORY_PROPERTY_NAME);

      List<String> actualMessages = actualViolations.stream().map(ConstraintViolation::getMessage).toList();

      Assertions.assertThat(actualViolations).isNotEmpty();
      Assertions.assertThat(actualMessages).contains("invalid category");
    }

  }

}