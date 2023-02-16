package br.com.emendes.financesapi.unit.dto.request;

import br.com.emendes.financesapi.dto.request.ChangePasswordRequest;
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

@DisplayName("Tests for ChangePasswordRequest")
class ChangePasswordRequestTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  private final String VALID_OLD_PASSWORD = "12345678@#$";
  private final String VALID_NEW_PASSWORD = "1234567890@#$";
  private final String VALID_CONFIRM = "1234567890@#$";

  @Nested
  @DisplayName("Tests for oldPassword validation")
  class OldPasswordValidation {

    private final String NEW_PASSWORD_PROPERTY_NAME = "oldPassword";

    private final ChangePasswordRequest.ChangePasswordRequestBuilder changePasswordRequestBuilder =
        ChangePasswordRequest.builder()
            .newPassword(VALID_NEW_PASSWORD)
            .confirm(VALID_CONFIRM);

    @ParameterizedTest
    @ValueSource(strings = {"12345678", "123456789012345678901234567890"})
    @DisplayName("Validate oldPassword must not return violations when oldPassword is valid")
    void validateOldPassword_MustNotReturnViolations_WhenOldPasswordIsValid(String validOldPassword) {
      ChangePasswordRequest changePasswordRequest = changePasswordRequestBuilder
          .oldPassword(validOldPassword)
          .build();

      Set<ConstraintViolation<ChangePasswordRequest>> actualViolations = validator
          .validateProperty(changePasswordRequest, NEW_PASSWORD_PROPERTY_NAME);

      Assertions.assertThat(actualViolations).isEmpty();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    @DisplayName("Validate oldPassword must return violations when oldPassword is blank")
    void validateOldPassword_MustReturnViolations_WhenOldPasswordIsInvalid(String blankOldPassword) {
      ChangePasswordRequest changePasswordRequest = changePasswordRequestBuilder
          .oldPassword(blankOldPassword)
          .build();

      Set<ConstraintViolation<ChangePasswordRequest>> actualViolations = validator
          .validateProperty(changePasswordRequest, NEW_PASSWORD_PROPERTY_NAME);

      List<String> actualMessages = actualViolations.stream().map(ConstraintViolation::getMessage).toList();

      Assertions.assertThat(actualViolations).isNotEmpty();
      Assertions.assertThat(actualMessages).contains("oldPassword must not be null or blank");
    }

  }

  @Nested
  @DisplayName("Tests for newPassword validation")
  class NewPasswordValidation {

    private final String NEW_PASSWORD_PROPERTY_NAME = "newPassword";

    private final ChangePasswordRequest.ChangePasswordRequestBuilder changePasswordRequestBuilder =
        ChangePasswordRequest.builder()
            .oldPassword(VALID_OLD_PASSWORD)
            .confirm(VALID_CONFIRM);

    @ParameterizedTest
    @ValueSource(strings = {"12345678", "123456789012345678901234567890"})
    @DisplayName("Validate newPassword must not return violations when newPassword is valid")
    void validateNewPassword_MustNotReturnViolations_WhenNewPasswordIsValid(String validNewPassword) {
      ChangePasswordRequest changePasswordRequest = changePasswordRequestBuilder
          .newPassword(validNewPassword)
          .build();

      Set<ConstraintViolation<ChangePasswordRequest>> actualViolations = validator
          .validateProperty(changePasswordRequest, NEW_PASSWORD_PROPERTY_NAME);

      Assertions.assertThat(actualViolations).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"1234567", "1234567890123456789012345678901"})
    @DisplayName("Validate newPassword must return violations when newPassword contains less than 8 and more than 30 characters")
    void validateNewPassword_MustReturnViolations_WhenNewPasswordContainsLessThan8AndMoreThan30Characters(String invalidNewPassword) {
      ChangePasswordRequest changePasswordRequest = changePasswordRequestBuilder
          .newPassword(invalidNewPassword)
          .build();

      Set<ConstraintViolation<ChangePasswordRequest>> actualViolations = validator
          .validateProperty(changePasswordRequest, NEW_PASSWORD_PROPERTY_NAME);
      List<String> actualMessages = actualViolations.stream().map(ConstraintViolation::getMessage).toList();

      Assertions.assertThat(actualViolations).isNotEmpty();
      Assertions.assertThat(actualMessages).contains("must contain between 8 and 30 characters");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    @DisplayName("Validate newPassword must return violations when newPassword is blank")
    void validateNewPassword_MustReturnViolations_WhenNewPasswordIsInvalid(String blankNewPassword) {
      ChangePasswordRequest changePasswordRequest = changePasswordRequestBuilder
          .newPassword(blankNewPassword)
          .build();

      Set<ConstraintViolation<ChangePasswordRequest>> actualViolations = validator
          .validateProperty(changePasswordRequest, NEW_PASSWORD_PROPERTY_NAME);

      List<String> actualMessages = actualViolations.stream().map(ConstraintViolation::getMessage).toList();

      Assertions.assertThat(actualViolations).isNotEmpty();
      Assertions.assertThat(actualMessages).contains("newPassword must not be null or blank");
    }

    @ParameterizedTest
    @ValueSource(strings = {"12345 6789", "12345\t6789", "12345\n6789"})
    @DisplayName("Validate newPassword must return violations when newPassword contains whitespace or tab or newline")
    void validateNewPassword_MustReturnViolations_WhenNewPasswordContainsWhitespaceOrTabOrNewLine(String invalidNewPassword) {
      ChangePasswordRequest changePasswordRequest = changePasswordRequestBuilder
          .newPassword(invalidNewPassword)
          .build();

      Set<ConstraintViolation<ChangePasswordRequest>> actualViolations = validator
          .validateProperty(changePasswordRequest, NEW_PASSWORD_PROPERTY_NAME);

      List<String> actualMessages = actualViolations.stream().map(ConstraintViolation::getMessage).toList();

      Assertions.assertThat(actualViolations).isNotEmpty();
      Assertions.assertThat(actualMessages).contains("newPassword must not contains whitespace, tab or newline");
    }

  }

  @Nested
  @DisplayName("Tests for confirm validation")
  class ConfirmValidation {

    private final String CONFIRM_PROPERTY_NAME = "confirm";

    private final ChangePasswordRequest.ChangePasswordRequestBuilder changePasswordRequestBuilder =
        ChangePasswordRequest.builder()
            .oldPassword(VALID_OLD_PASSWORD)
            .newPassword(VALID_NEW_PASSWORD);

    @Test
    @DisplayName("Validate confirm must not return violations when confirm is valid")
    void validateConfirm_MustNotReturnViolations_WhenConfirmIsValid() {
      ChangePasswordRequest changePasswordRequest = changePasswordRequestBuilder
          .confirm("1234567890@#$")
          .build();

      Set<ConstraintViolation<ChangePasswordRequest>> actualViolations = validator
          .validateProperty(changePasswordRequest, CONFIRM_PROPERTY_NAME);

      Assertions.assertThat(actualViolations).isEmpty();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    @DisplayName("Validate confirm must return violations when confirm is blank")
    void validateConfirm_MustReturnViolations_WhenConfirmIsInvalid(String blankConfirm) {
      ChangePasswordRequest changePasswordRequest = changePasswordRequestBuilder
          .confirm(blankConfirm)
          .build();

      Set<ConstraintViolation<ChangePasswordRequest>> actualViolations = validator
          .validateProperty(changePasswordRequest, CONFIRM_PROPERTY_NAME);

      List<String> actualMessages = actualViolations.stream().map(ConstraintViolation::getMessage).toList();

      Assertions.assertThat(actualViolations).isNotEmpty();
      Assertions.assertThat(actualMessages).contains("confirm must not be null or blank");
    }

  }

}