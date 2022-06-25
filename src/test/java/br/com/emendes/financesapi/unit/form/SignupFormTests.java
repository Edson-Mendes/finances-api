package br.com.emendes.financesapi.unit.form;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.emendes.financesapi.controller.form.SignupForm;

@DisplayName("Tests for SignupForm")
public class SignupFormTests {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  @DisplayName("must return empty violations when SignupForm is valid")
  void mustReturnEmptyViolations_WhenSignupFormIsValid() {
    String name = "Lorem Ipsum";
    String email = "lorem@email.com";
    String password = "123456789O";
    String confirm = "123456789O";
    SignupForm signupForm = new SignupForm(name, email, password, confirm);

    Set<ConstraintViolation<SignupForm>> violations = validator.validate(signupForm);

    Assertions.assertThat(violations).isEmpty();
  }

  @Test
  @DisplayName("must return violations when Name is null")
  void mustReturnViolations_WhenNameIsNull() {
    String name = null;
    String email = "lorem@email.com";
    String password = "123456789O";
    String confirm = "123456789O";
    SignupForm signupForm = new SignupForm(name, email, password, confirm);

    Set<ConstraintViolation<SignupForm>> violations = validator.validate(signupForm);

    Assertions.assertThat(violations)
        .isNotEmpty()
        .hasSize(1);
  }

  @Test
  @DisplayName("must return violations when Name is blank")
  void mustReturnViolations_WhenNameIsBlank() {
    String name = "";
    String email = "lorem@email.com";
    String password = "123456789O";
    String confirm = "123456789O";
    SignupForm signupForm = new SignupForm(name, email, password, confirm);

    Set<ConstraintViolation<SignupForm>> violations = validator.validate(signupForm);

    Assertions.assertThat(violations)
        .isNotEmpty()
        .hasSize(1);
  }

  @Test
  @DisplayName("must return violations when email is invalid")
  void mustReturnViolations_WhenEmailIsInvalid() {
    String name = "Lorem Ipsum";
    String email = "lorememailcom";
    String password = "123456789O";
    String confirm = "123456789O";
    SignupForm signupForm = new SignupForm(name, email, password, confirm);

    Set<ConstraintViolation<SignupForm>> violations = validator.validate(signupForm);

    Assertions.assertThat(violations)
        .isNotEmpty()
        .hasSize(1);
  }

  @Test
  @DisplayName("must return violations when email is null")
  void mustReturnViolations_WhenEmailIsNull() {
    String name = "Lorem Ipsum";
    String email = null;
    String password = "123456789O";
    String confirm = "123456789O";
    SignupForm signupForm = new SignupForm(name, email, password, confirm);

    Set<ConstraintViolation<SignupForm>> violations = validator.validate(signupForm);

    Assertions.assertThat(violations)
        .isNotEmpty()
        .hasSize(1);
  }

  @Test
  @DisplayName("must return violations when email is blank")
  void mustReturnViolations_WhenEmailIsBlank() {
    String name = "Lorem Ipsum";
    String email = "";
    String password = "123456789O";
    String confirm = "123456789O";
    SignupForm signupForm = new SignupForm(name, email, password, confirm);

    Set<ConstraintViolation<SignupForm>> violations = validator.validate(signupForm);

    Assertions.assertThat(violations)
        .isNotEmpty()
        .hasSize(1);
  }

  @Test
  @DisplayName("must return violations when password is invalid")
  void mustReturnViolations_WhenPasswordIsInvalid() {
    String name = "Lorem Ipsum";
    String email = "lorem@email.com";
    String password = "abcdefghij";
    String confirm = "abcdefghij";
    SignupForm signupForm = new SignupForm(name, email, password, confirm);

    Set<ConstraintViolation<SignupForm>> violations = validator.validate(signupForm);

    Assertions.assertThat(violations)
        .isNotEmpty()
        .hasSize(2);
  }

  @Test
  @DisplayName("must return violations when password is null")
  void mustReturnViolations_WhenPasswordIsNull() {
    String name = "Lorem Ipsum";
    String email = "lorem@email.com";
    String password = null;
    String confirm = "abcdefghij";
    SignupForm signupForm = new SignupForm(name, email, password, confirm);

    Set<ConstraintViolation<SignupForm>> violations = validator.validate(signupForm);

    Assertions.assertThat(violations)
        .isNotEmpty()
        .hasSize(1);
  }

  @Test
  @DisplayName("must return violations when confimr is null")
  void mustReturnViolations_WhenConfirmIsNull() {
    String name = "Lorem Ipsum";
    String email = "lorem@email.com";
    String password = "abcdefghij0";
    String confirm = null;
    SignupForm signupForm = new SignupForm(name, email, password, confirm);

    Set<ConstraintViolation<SignupForm>> violations = validator.validate(signupForm);

    Assertions.assertThat(violations)
        .isNotEmpty()
        .hasSize(1);
  }

  @Test
  @DisplayName("must return violations when confimr is blank")
  void mustReturnViolations_WhenConfirmIsBlank() {
    String name = "Lorem Ipsum";
    String email = "lorem@email.com";
    String password = "abcdefghij0";
    String confirm = "     ";
    SignupForm signupForm = new SignupForm(name, email, password, confirm);

    Set<ConstraintViolation<SignupForm>> violations = validator.validate(signupForm);

    Assertions.assertThat(violations)
        .isNotEmpty()
        .hasSize(1);
  }

}
