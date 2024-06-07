package br.com.emendes.financesapi.unit.dto.request;

import br.com.emendes.financesapi.dto.request.SignInRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Set;

@DisplayName("Tests for SignInRequest")
class SignInRequestTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  private final String VALID_PASSWORD = "1234567890";
  private final String VALID_EMAIL = "lorem@email.com";

  @Nested
  @DisplayName("Tests for email validation")
  class EmailValidation {

    private final String EMAIL_PROPERTY_NAME = "email";

    @Test
    @DisplayName("Validate email must not return violations when email is valid")
    void validateEmail_MustNotReturnViolations_WhenEmailIsValid() {
      SignInRequest signInRequest = SignInRequest.builder()
          .email("lorem@email.com")
          .password(VALID_PASSWORD)
          .build();

      Set<ConstraintViolation<SignInRequest>> actualViolations = validator
          .validateProperty(signInRequest, EMAIL_PROPERTY_NAME);

      Assertions.assertThat(actualViolations).isEmpty();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    @DisplayName("Validate email must return violations when email is blank")
    void validateEmail_MustReturnViolations_WhenEmailIsInvalid(String blankEmail) {
      SignInRequest signInRequest = SignInRequest.builder()
          .email(blankEmail)
          .password(VALID_PASSWORD)
          .build();

      Set<ConstraintViolation<SignInRequest>> actualViolations = validator
          .validateProperty(signInRequest, EMAIL_PROPERTY_NAME);

      List<String> actualMessages = actualViolations.stream().map(ConstraintViolation::getMessage).toList();

      Assertions.assertThat(actualViolations).isNotEmpty();
      Assertions.assertThat(actualMessages).contains("email must not be null or blank");
    }

    @ParameterizedTest
    @ValueSource(strings = {"lorememailcom", "lorem.com", "@email.com"})
    @DisplayName("Validate email must return violations when email is not well formed")
    void validateEmail_MustReturnViolations_WhenEmailIsNotWellFormed(String invalidEmail) {
      SignInRequest signInRequest = SignInRequest.builder()
          .email(invalidEmail)
          .password(VALID_PASSWORD)
          .build();

      Set<ConstraintViolation<SignInRequest>> actualViolations = validator
          .validateProperty(signInRequest, EMAIL_PROPERTY_NAME);

      List<String> actualMessages = actualViolations.stream().map(ConstraintViolation::getMessage).toList();

      Assertions.assertThat(actualViolations).isNotEmpty();
      Assertions.assertThat(actualMessages).contains("must be a well formed email");
    }

  }

  @Nested
  @DisplayName("Tests for password validation")
  class PasswordValidation {

    private final String PASSWORD_PROPERTY_NAME = "password";

    @Test
    @DisplayName("Validate password must not return violations when password is valid")
    void validatePassword_MustNotReturnViolations_WhenPasswordIsValid() {
      SignInRequest signInRequest = SignInRequest.builder()
          .email(VALID_EMAIL)
          .password("123456789")
          .build();

      Set<ConstraintViolation<SignInRequest>> actualViolations = validator
          .validateProperty(signInRequest, PASSWORD_PROPERTY_NAME);

      Assertions.assertThat(actualViolations).isEmpty();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    @DisplayName("Validate password must return violations when password is blank")
    void validatePassword_MustReturnViolations_WhenPasswordIsInvalid(String blankPassword) {
      SignInRequest signInRequest = SignInRequest.builder()
          .email(VALID_EMAIL)
          .password(blankPassword)
          .build();

      Set<ConstraintViolation<SignInRequest>> actualViolations = validator
          .validateProperty(signInRequest, PASSWORD_PROPERTY_NAME);

      List<String> actualMessages = actualViolations.stream().map(ConstraintViolation::getMessage).toList();

      Assertions.assertThat(actualViolations).isNotEmpty();
      Assertions.assertThat(actualMessages).contains("password must not be null or blank");
    }

  }

}
