package br.com.emendes.financesapi.unit.dto.request;

import br.com.emendes.financesapi.dto.request.IncomeRequest;
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

@DisplayName("Tests for IncomeRequest")
class IncomeRequestTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  private final String VALID_DESCRIPTION = "Salário";
  private final String VALID_DATE = "2023-02-05";
  private final BigDecimal VALID_VALUE = new BigDecimal("2500.00");

  @Nested
  @DisplayName("Tests for description validation")
  class DescriptionValidation {

    private final String DESCRIPTION_PROPERTY_NAME = "description";
    private final IncomeRequest.IncomeRequestBuilder incomeRequestBuilder = IncomeRequest.builder()
        .date(VALID_DATE)
        .value(VALID_VALUE);

    @Test
    @DisplayName("Validate description must not return violations when description is valid")
    void validateDescription_MustNotReturnViolations_WhenDescriptionIsValid() {
      IncomeRequest incomeRequest = incomeRequestBuilder
          .description("Salário")
          .build();

      Set<ConstraintViolation<IncomeRequest>> actualViolations = validator
          .validateProperty(incomeRequest, DESCRIPTION_PROPERTY_NAME);

      Assertions.assertThat(actualViolations).isEmpty();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    @DisplayName("Validate description must return violations when description is blank")
    void validateDescription_MustReturnViolations_WhenDescriptionIsBlank(String blankDescription) {
      IncomeRequest incomeRequest = incomeRequestBuilder
          .description(blankDescription)
          .build();

      Set<ConstraintViolation<IncomeRequest>> actualViolations = validator
          .validateProperty(incomeRequest, DESCRIPTION_PROPERTY_NAME);

      List<String> actualMessages = actualViolations.stream().map(ConstraintViolation::getMessage).toList();

      Assertions.assertThat(actualViolations).isNotEmpty();
      Assertions.assertThat(actualMessages).contains("description must not be null or blank");
    }

    @Test
    @DisplayName("Validate description must return violations when description contains more than 255 characters")
    void validateDescription_MustReturnViolations_WhenDescriptionContainsMoreThan255Characters() {
      String descriptionWithMoreThan255Characters = "SalárioSalárioSalárioSalárioSalárioSalárioSalário" +
          "SalárioSalárioSalárioSalárioSalárioSalárioSalárioSalárioSalárioSalário" +
          "SalárioSalárioSalárioSalárioSalárioSalárioSalárioSalárioSalárioSalário" +
          "SalárioSalárioSalárioSalárioSalárioSalárioSalárioSalárioSalárioSalário";

      IncomeRequest incomeRequest = incomeRequestBuilder
          .description(descriptionWithMoreThan255Characters)
          .build();

      Set<ConstraintViolation<IncomeRequest>> actualViolations = validator
          .validateProperty(incomeRequest, DESCRIPTION_PROPERTY_NAME);
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

    private final IncomeRequest.IncomeRequestBuilder incomeRequestBuilder = IncomeRequest.builder()
        .description(VALID_DESCRIPTION)
        .value(VALID_VALUE);

    @Test
    @DisplayName("Validate date must not return violations when date is valid")
    void validateDate_MustNotReturnViolations_WhenDateIsValid() {
      IncomeRequest incomeRequest = incomeRequestBuilder
          .date("2023-02-05")
          .build();

      Set<ConstraintViolation<IncomeRequest>> actualViolations = validator
          .validateProperty(incomeRequest, DATE_PROPERTY_NAME);

      Assertions.assertThat(actualViolations).isEmpty();
    }

    @Test
    @DisplayName("Validate date must return Violations when date is null")
    void validateDate_MustReturnViolations_WhenDateIsNull() {
      IncomeRequest incomeRequest = incomeRequestBuilder
          .date(null)
          .build();

      Set<ConstraintViolation<IncomeRequest>> actualViolations = validator
          .validateProperty(incomeRequest, DATE_PROPERTY_NAME);
      List<String> actualMessages = actualViolations.stream().map(ConstraintViolation::getMessage).toList();

      Assertions.assertThat(actualViolations).isNotEmpty();
      Assertions.assertThat(actualMessages).contains("date must not be null");
    }

    @ParameterizedTest
    @ValueSource(strings = {"05/02/2023", "05-02-2023", "20230205", "2023-02", "2023-02-"})
    @DisplayName("Validate date must return violations when date is invalid")
    void validateDate_MustReturnViolations_WhenDateIsInvalid(String invalidDate) {
      IncomeRequest incomeRequest = incomeRequestBuilder
          .date(invalidDate)
          .build();

      Set<ConstraintViolation<IncomeRequest>> actualViolations = validator
          .validateProperty(incomeRequest, DATE_PROPERTY_NAME);
      List<String> actualMessages = actualViolations.stream().map(ConstraintViolation::getMessage).toList();

      Assertions.assertThat(actualViolations).isNotEmpty();
      Assertions.assertThat(actualMessages).contains("Invalid date");
    }

  }

  @Nested
  @DisplayName("Tests for value validation")
  class ValueValidation {

    private final String VALUE_PROPERTY_NAME = "value";

    private final IncomeRequest.IncomeRequestBuilder incomeRequestBuilder = IncomeRequest.builder()
        .description(VALID_DESCRIPTION)
        .date(VALID_DATE);

    @ParameterizedTest
    @ValueSource(strings = {"1500.99", "1500", "150000.99", "0.99"})
    @DisplayName("Validate value must not return violations when value is valid")
    void validateValue_MustNotReturnViolations_WhenValueIsValid(String validValue) {
      IncomeRequest incomeRequest = incomeRequestBuilder
          .value(new BigDecimal(validValue))
          .build();

      Set<ConstraintViolation<IncomeRequest>> actualViolations = validator
          .validateProperty(incomeRequest, VALUE_PROPERTY_NAME);

      Assertions.assertThat(actualViolations).isEmpty();
    }

    @Test
    @DisplayName("Validate value must return Violations value is null")
    void validateValue_MustReturnViolations_WhenValueIsNull() {
      IncomeRequest incomeRequest = incomeRequestBuilder
          .value(null)
          .build();

      Set<ConstraintViolation<IncomeRequest>> actualViolations = validator
          .validateProperty(incomeRequest, VALUE_PROPERTY_NAME);
      List<String> actualMessages = actualViolations.stream().map(ConstraintViolation::getMessage).toList();

      Assertions.assertThat(actualViolations).isNotEmpty();
      Assertions.assertThat(actualMessages).contains("value must not be null");
    }

    @Test
    @DisplayName("Validate value must return Violations when value is negative")
    void validateValue_MustReturnViolations_WhenValueIsNegative() {
      IncomeRequest incomeRequest = incomeRequestBuilder
          .value(new BigDecimal("-1500.00"))
          .build();

      Set<ConstraintViolation<IncomeRequest>> actualViolations = validator
          .validateProperty(incomeRequest, VALUE_PROPERTY_NAME);
      List<String> actualMessages = actualViolations.stream().map(ConstraintViolation::getMessage).toList();

      Assertions.assertThat(actualViolations).isNotEmpty();
      Assertions.assertThat(actualMessages).contains("value must be positive");
    }

    @Test
    @DisplayName("Validate value must return Violations when value's integer part has more than 6 digits")
    void validateValue_MustReturnViolations_WhenValuesIntegerPartHasMoreThan6Digits() {
      IncomeRequest incomeRequest = incomeRequestBuilder
          .value(new BigDecimal("1500000.00"))
          .build();

      Set<ConstraintViolation<IncomeRequest>> actualViolations = validator
          .validateProperty(incomeRequest, VALUE_PROPERTY_NAME);
      List<String> actualMessages = actualViolations.stream().map(ConstraintViolation::getMessage).toList();

      Assertions.assertThat(actualViolations).isNotEmpty();
      Assertions.assertThat(actualMessages)
          .contains("Integer part must be max 6 digits and fraction part must be max 2 digits");
    }

    @Test
    @DisplayName("Validate value must return Violations when value's fraction part has more than 2 digits")
    void validateValue_MustReturnViolations_WhenValuesFractionPartHasMoreThan6Digits() {
      IncomeRequest incomeRequest = incomeRequestBuilder
          .value(new BigDecimal("1500.999"))
          .build();

      Set<ConstraintViolation<IncomeRequest>> actualViolations = validator
          .validateProperty(incomeRequest, VALUE_PROPERTY_NAME);
      List<String> actualMessages = actualViolations.stream().map(ConstraintViolation::getMessage).toList();

      Assertions.assertThat(actualViolations).isNotEmpty();
      Assertions.assertThat(actualMessages)
          .contains("Integer part must be max 6 digits and fraction part must be max 2 digits");
    }

  }

}