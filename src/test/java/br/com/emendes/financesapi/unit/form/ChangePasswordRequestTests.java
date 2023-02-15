package br.com.emendes.financesapi.unit.form;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.emendes.financesapi.dto.request.ChangePasswordRequest;

@DisplayName("Tests for ChangePasswordForm")
class ChangePasswordRequestTests {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  @DisplayName("Must return empty violations when ChangePasswordForm is valid")
  void mustReturnEmptyViolations_WhenChangePasswordFormIsValid() {
    String oldPassword = "123456";
    String newPassword = "12345678oo";
    String confirm = "12345678oo";
    ChangePasswordRequest validChangePasswordRequest = new ChangePasswordRequest(oldPassword, newPassword, confirm);

    Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(validChangePasswordRequest);

    Assertions.assertThat(violations).isEmpty();
  }

  @Test
  @DisplayName("Must return violations when newPassword is null")
  void mustReturnViolations_WhenNewPasswordIsNull() {
    String oldPassword = "123456";
    String newPassword = null;
    String confirm = "12345678oo";
    ChangePasswordRequest invalidChangePasswordRequest = new ChangePasswordRequest(oldPassword, newPassword, confirm);

    Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(invalidChangePasswordRequest);

    Assertions.assertThat(violations)
        .isNotEmpty()
        .hasSize(2);
  }

  @Test
  @DisplayName("Must return violations when confirm is null")
  void mustReturnViolations_WhenConfirmIsNull() {
    String oldPassword = "123456";
    String newPassword = "12345678oo";
    String confirm = null;
    ChangePasswordRequest invalidChangePasswordRequest = new ChangePasswordRequest(oldPassword, newPassword, confirm);

    Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(invalidChangePasswordRequest);

    Assertions.assertThat(violations)
        .isNotEmpty()
        .hasSize(1);
  }

  @Test
  @DisplayName("Must return violations when oldPassword is blank")
  void mustReturnViolations_WhenOldPasswordIsBlank() {
    String oldPassword = "";
    String newPassword = "12345678oo";
    String confirm = "12345678oo";
    ChangePasswordRequest invalidChangePasswordRequest = new ChangePasswordRequest(oldPassword, newPassword, confirm);

    Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(invalidChangePasswordRequest);

    Assertions.assertThat(violations)
        .isNotEmpty()
        .hasSize(1);
  }

  @Test
  @DisplayName("Must return violations when fields are null")
  void mustReturnViolations_WhenFieldsAreNull() {
    String oldPassword = null;
    String newPassword = null;
    String confirm = null;
    ChangePasswordRequest invalidChangePasswordRequest = new ChangePasswordRequest(oldPassword, newPassword, confirm);

    Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(invalidChangePasswordRequest);

    Assertions.assertThat(violations)
        .isNotEmpty()
        .hasSize(4);
  }

}
