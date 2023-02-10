package br.com.emendes.financesapi.unit.form;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.emendes.financesapi.dto.request.SignInRequest;

@DisplayName("Tests for LoginForm")
public class SignInRequestTests {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  @DisplayName("Must return empty violations when LoginForm is valid")
  void mustReturnEmptyViolations_WhenLoginFormIsValid() {
    String email = "lorem@email.com";
    String password = "123456789O";

    SignInRequest signInRequest = new SignInRequest(email, password);

    Set<ConstraintViolation<SignInRequest>> violations = validator.validate(signInRequest);

    Assertions.assertThat(violations).isEmpty();
  }

  @Test
  @DisplayName("Must return violations when email is invalid")
  void mustReturnViolations_WhenEmailIsInvalid() {
    String email = "invalidemailcom";
    String password = "123456789O";

    SignInRequest signInRequest = new SignInRequest(email, password);

    Set<ConstraintViolation<SignInRequest>> violations = validator.validate(signInRequest);

    Assertions.assertThat(violations)
        .isNotEmpty()
        .hasSize(1);
  }

  @Test
  @DisplayName("Must return violations when email is null")
  void mustReturnViolations_WhenEmailIsNull() {
    String email = null;
    String password = "123456789O";

    SignInRequest signInRequest = new SignInRequest(email, password);

    Set<ConstraintViolation<SignInRequest>> violations = validator.validate(signInRequest);

    Assertions.assertThat(violations)
        .isNotEmpty()
        .hasSize(1);
  }

  @Test
  @DisplayName("Must return violations when password is null")
  void mustReturnViolations_WhenPasswordIsNull() {
    String email = "lorem@email.com";
    String password = null;

    SignInRequest signInRequest = new SignInRequest(email, password);

    Set<ConstraintViolation<SignInRequest>> violations = validator.validate(signInRequest);

    Assertions.assertThat(violations)
        .isNotEmpty()
        .hasSize(1);
  }

  @Test
  @DisplayName("Must return violations when password is blank")
  void mustReturnViolations_WhenPasswordIsBlank() {
    String email = "lorem@email.com";
    String password = "";

    SignInRequest signInRequest = new SignInRequest(email, password);

    Set<ConstraintViolation<SignInRequest>> violations = validator.validate(signInRequest);

    Assertions.assertThat(violations)
        .isNotEmpty()
        .hasSize(1);
  }

}
