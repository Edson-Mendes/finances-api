package br.com.emendes.financesapi.unit.dto.request;

import br.com.emendes.financesapi.dto.request.SignupRequest;
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
import java.util.List;
import java.util.Set;

@DisplayName("Tests for SignupRequest")
class SignupRequestTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  private final String VALID_NAME = "Lorem Ipsum";
  private final String VALID_EMAIL = "lorem@email.com";
  private final String VALID_PASSWORD = "1234567890";

  @Nested
  @DisplayName("Tests for name validation")
  class NameValidation {

    private final String NAME_PROPERTY_NAME = "name";

    @Test
    @DisplayName("Validate name must not return violations when name is valid")
    void validateName_MustNotReturnViolations_WhenNameIsValid() {
      SignupRequest signupRequest = SignupRequest.builder()
          .name("Lorem Ipsum")
          .email(VALID_EMAIL)
          .password(VALID_PASSWORD)
          .confirm(VALID_PASSWORD)
          .build();

      Set<ConstraintViolation<SignupRequest>> actualViolations = validator
          .validateProperty(signupRequest, NAME_PROPERTY_NAME);

      Assertions.assertThat(actualViolations).isEmpty();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    @DisplayName("Validate name must return violations when name is blank")
    void validateName_MustReturnViolations_WhenNameIsInvalid(String blankName) {
      SignupRequest signupRequest = SignupRequest.builder()
          .name(blankName)
          .email(VALID_EMAIL)
          .password(VALID_PASSWORD)
          .confirm(VALID_PASSWORD)
          .build();

      Set<ConstraintViolation<SignupRequest>> actualViolations = validator
          .validateProperty(signupRequest, NAME_PROPERTY_NAME);

      List<String> actualMessages = actualViolations.stream().map(ConstraintViolation::getMessage).toList();

      Assertions.assertThat(actualViolations).isNotEmpty();
      Assertions.assertThat(actualMessages).contains("name must not be null or blank");
    }

    @Test
    @DisplayName("Validate name must return violations when name contains more than 100 characters")
    void validateName_MustReturnViolations_WhenNameContainsMoreThan100Characters() {
      String nameWithMoreThan100Characters = "loremloremloremloremloremloremloremloremloremlorem" +
          "loremloremloremloremloremloremloremloremloremloremlorem";
      SignupRequest signupRequest = SignupRequest.builder()
          .name(nameWithMoreThan100Characters)
          .email(VALID_EMAIL)
          .password(VALID_PASSWORD)
          .confirm(VALID_PASSWORD)
          .build();

      Set<ConstraintViolation<SignupRequest>> actualViolations = validator
          .validateProperty(signupRequest, NAME_PROPERTY_NAME);
      List<String> actualMessages = actualViolations.stream().map(ConstraintViolation::getMessage).toList();

      Assertions.assertThat(actualViolations).isNotEmpty();
      Assertions.assertThat(actualMessages).contains("name must contain max 100 characters");
    }

  }

  @Nested
  @DisplayName("Tests for email validation")
  class EmailValidation {

    private final String EMAIL_PROPERTY_NAME = "email";

    @Test
    @DisplayName("Validate email must not return violations when email is valid")
    void validateEmail_MustNotReturnViolations_WhenEmailIsValid() {
      SignupRequest signupRequest = SignupRequest.builder()
          .email("lorem@email.com")
          .name(VALID_NAME)
          .password(VALID_PASSWORD)
          .confirm(VALID_PASSWORD)
          .build();

      Set<ConstraintViolation<SignupRequest>> actualViolations = validator
          .validateProperty(signupRequest, EMAIL_PROPERTY_NAME);

      Assertions.assertThat(actualViolations).isEmpty();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    @DisplayName("Validate email must return violations when email is blank")
    void validateEmail_MustReturnViolations_WhenEmailIsInvalid(String blankEmail) {
      SignupRequest signupRequest = SignupRequest.builder()
          .email(blankEmail)
          .name(VALID_NAME)
          .password(VALID_PASSWORD)
          .confirm(VALID_PASSWORD)
          .build();

      Set<ConstraintViolation<SignupRequest>> actualViolations = validator
          .validateProperty(signupRequest, EMAIL_PROPERTY_NAME);

      List<String> actualMessages = actualViolations.stream().map(ConstraintViolation::getMessage).toList();

      Assertions.assertThat(actualViolations).isNotEmpty();
      Assertions.assertThat(actualMessages).contains("email must not be null or blank");
    }

    @ParameterizedTest
    @ValueSource(strings = {"lorememailcom", "lorem.com", "@email.com"})
    @DisplayName("Validate email must return violations when email is not well formed")
    void validateEmail_MustReturnViolations_WhenEmailIsNotWellFormed(String invalidEmail) {
      SignupRequest signupRequest = SignupRequest.builder()
          .email(invalidEmail)
          .name(VALID_NAME)
          .password(VALID_PASSWORD)
          .confirm(VALID_PASSWORD)
          .build();

      Set<ConstraintViolation<SignupRequest>> actualViolations = validator
          .validateProperty(signupRequest, EMAIL_PROPERTY_NAME);

      List<String> actualMessages = actualViolations.stream().map(ConstraintViolation::getMessage).toList();

      Assertions.assertThat(actualViolations).isNotEmpty();
      Assertions.assertThat(actualMessages).contains("must be a well formed email");
    }

    @Test
    @DisplayName("Validate email must return violations when email contains more than 150 characters")
    void validateEmail_MustReturnViolations_WhenEmailContainsMoreThan150Characters() {
      String emailWithMoreThan150Characters = "loremloremloremloremloremloremloremloremloremlorem" +
          "loremloremloremloremloremloremloremloremloremlorem" +
          "loremloremloremloremloremloremloremloremloremlorem" +
          "lorem@email.com";
      SignupRequest signupRequest = SignupRequest.builder()
          .email(emailWithMoreThan150Characters)
          .name(VALID_NAME)
          .password(VALID_PASSWORD)
          .confirm(VALID_PASSWORD)
          .build();

      Set<ConstraintViolation<SignupRequest>> actualViolations = validator
          .validateProperty(signupRequest, EMAIL_PROPERTY_NAME);
      List<String> actualMessages = actualViolations.stream().map(ConstraintViolation::getMessage).toList();

      Assertions.assertThat(actualViolations).isNotEmpty();
      Assertions.assertThat(actualMessages).contains("email must contain max 150 characters");
    }

  }

  @Nested
  @DisplayName("Tests for password validation")
  class PasswordValidation {

    private final String PASSWORD_PROPERTY_NAME = "password";

    @ParameterizedTest
    @ValueSource(strings = {"12345678", "123456789012345678901234567890"})
    @DisplayName("Validate password must not return violations when password is valid")
    void validatePassword_MustNotReturnViolations_WhenPasswordIsValid(String validPassword) {
      SignupRequest signupRequest = SignupRequest.builder()
          .password(validPassword)
          .name(VALID_NAME)
          .email(VALID_EMAIL)
          .confirm(VALID_PASSWORD)
          .build();

      Set<ConstraintViolation<SignupRequest>> actualViolations = validator
          .validateProperty(signupRequest, PASSWORD_PROPERTY_NAME);

      Assertions.assertThat(actualViolations).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"1234567", "1234567890123456789012345678901"})
    @DisplayName("Validate password must return violations when password contains less than 8 and more than 30 characters")
    void validatePassword_MustReturnViolations_WhenPasswordContainsLessThan8AndMoreThan30Characters(String invalidPassword) {
      SignupRequest signupRequest = SignupRequest.builder()
          .password(invalidPassword)
          .name(VALID_NAME)
          .email(VALID_EMAIL)
          .confirm(VALID_PASSWORD)
          .build();

      Set<ConstraintViolation<SignupRequest>> actualViolations = validator
          .validateProperty(signupRequest, PASSWORD_PROPERTY_NAME);
      List<String> actualMessages = actualViolations.stream().map(ConstraintViolation::getMessage).toList();

      Assertions.assertThat(actualViolations).isNotEmpty();
      Assertions.assertThat(actualMessages).contains("must contain between 8 and 30 characters");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    @DisplayName("Validate password must return violations when password is blank")
    void validatePassword_MustReturnViolations_WhenPasswordIsInvalid(String blankPassword) {
      SignupRequest signupRequest = SignupRequest.builder()
          .password(blankPassword)
          .name(VALID_NAME)
          .email(VALID_EMAIL)
          .confirm(VALID_PASSWORD)
          .build();

      Set<ConstraintViolation<SignupRequest>> actualViolations = validator
          .validateProperty(signupRequest, PASSWORD_PROPERTY_NAME);

      List<String> actualMessages = actualViolations.stream().map(ConstraintViolation::getMessage).toList();

      Assertions.assertThat(actualViolations).isNotEmpty();
      Assertions.assertThat(actualMessages).contains("password must not be null or blank");
    }

    @ParameterizedTest
    @ValueSource(strings = {"12345 6789", "12345\t6789", "12345\n6789"})
    @DisplayName("Validate password must return violations when password contains whitespace or tab or newline")
    void validatePassword_MustReturnViolations_WhenPasswordContainsWhitespaceOrTabOrNewLine(String invalidPassword) {
      SignupRequest signupRequest = SignupRequest.builder()
          .password(invalidPassword)
          .name(VALID_NAME)
          .email(VALID_EMAIL)
          .confirm(VALID_PASSWORD)
          .build();

      Set<ConstraintViolation<SignupRequest>> actualViolations = validator
          .validateProperty(signupRequest, PASSWORD_PROPERTY_NAME);

      List<String> actualMessages = actualViolations.stream().map(ConstraintViolation::getMessage).toList();

      Assertions.assertThat(actualViolations).isNotEmpty();
      Assertions.assertThat(actualMessages).contains("password must not contains whitespace, tab or newline");
    }

  }

  @Nested
  @DisplayName("Tests for confirm validation")
  class ConfirmValidation {

    private final String CONFIRM_PROPERTY_NAME = "confirm";

    @Test
    @DisplayName("Validate confirm must not return violations when confirm is valid")
    void validateConfirm_MustNotReturnViolations_WhenConfirmIsValid() {
      SignupRequest signupRequest = SignupRequest.builder()
          .confirm("123456789")
          .name(VALID_NAME)
          .email(VALID_EMAIL)
          .password(VALID_PASSWORD)
          .build();

      Set<ConstraintViolation<SignupRequest>> actualViolations = validator
          .validateProperty(signupRequest, CONFIRM_PROPERTY_NAME);

      Assertions.assertThat(actualViolations).isEmpty();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    @DisplayName("Validate confirm must return violations when confirm is blank")
    void validateConfirm_MustReturnViolations_WhenConfirmIsInvalid(String blankConfirm) {
      SignupRequest signupRequest = SignupRequest.builder()
          .confirm(blankConfirm)
          .name(VALID_NAME)
          .email(VALID_EMAIL)
          .password(VALID_PASSWORD)
          .build();

      Set<ConstraintViolation<SignupRequest>> actualViolations = validator
          .validateProperty(signupRequest, CONFIRM_PROPERTY_NAME);

      List<String> actualMessages = actualViolations.stream().map(ConstraintViolation::getMessage).toList();

      Assertions.assertThat(actualViolations).isNotEmpty();
      Assertions.assertThat(actualMessages).contains("confirm must not be null or blank");
    }

  }

}
