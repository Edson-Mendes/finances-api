package br.com.emendes.financesapi.form;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.emendes.financesapi.controller.form.ChangePasswordForm;

@DisplayName("Tests for ChangePasswordForm")
public class ChangePasswordFormTests {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  @DisplayName("Must return empty violations when ChangePasswordForm is valid")
  void mustReturnEmptyViolations_WhenChangePasswordFormIsValid() {
    String newPassword = "12345678oo";
    String confirm = "12345678oo";
    ChangePasswordForm validChangePasswordForm = new ChangePasswordForm(newPassword, confirm);

    Set<ConstraintViolation<ChangePasswordForm>> violations = validator.validate(validChangePasswordForm);

    Assertions.assertThat(violations).isEmpty();
  }

  @Test
  @DisplayName("Must return violations when newPassword is null")
  void mustReturnViolations_WhenNewPasswordIsNull() {
    String newPassword = null;
    String confirm = "12345678oo";
    ChangePasswordForm invalidChangePasswordForm = new ChangePasswordForm(newPassword, confirm);

    Set<ConstraintViolation<ChangePasswordForm>> violations = validator.validate(invalidChangePasswordForm);

    Assertions.assertThat(violations)
        .isNotEmpty()
        .hasSize(2);
  }

  @Test
  @DisplayName("Must return violations when confirm is null")
  void mustReturnViolations_WhenConfirmIsNull() {
    String newPassword = "12345678oo";
    String confirm = null;
    ChangePasswordForm invalidChangePasswordForm = new ChangePasswordForm(newPassword, confirm);

    Set<ConstraintViolation<ChangePasswordForm>> violations = validator.validate(invalidChangePasswordForm);

    Assertions.assertThat(violations)
        .isNotEmpty()
        .hasSize(1);
  }

  @Test
  @DisplayName("Must return violations when confirm and newPassword are null")
  void mustReturnViolations_WhenConfirmAndNewPasswordAreNull() {
    String newPassword = null;
    String confirm = null;
    ChangePasswordForm invalidChangePasswordForm = new ChangePasswordForm(newPassword, confirm);

    Set<ConstraintViolation<ChangePasswordForm>> violations = validator.validate(invalidChangePasswordForm);

    Assertions.assertThat(violations)
        .isNotEmpty()
        .hasSize(3);
  }

}
