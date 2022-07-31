package br.com.emendes.financesapi.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import br.com.emendes.financesapi.validation.annotation.ValidPassword;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

  @Override
  public boolean isValid(String password, ConstraintValidatorContext context) {
    if (password == null) {
      return false;
    }
    if (isTooSmall(password)) {
      context.buildConstraintViolationWithTemplate("deve ter de 8 a 30 caracteres.")
          .addConstraintViolation();
      return false;
    }
    if (notHasNumber(password)) {
      context.buildConstraintViolationWithTemplate("deve ter pelo menos um número.")
          .addConstraintViolation();
      return false;
    }
    if (hasWhiteSpace(password)) {
      context.buildConstraintViolationWithTemplate("não deve ter espaço em branco.")
          .addConstraintViolation();
      return false;
    }
    return true;
  }

  private boolean isTooSmall(String password) {
    return password.length() < 8 || password.length() > 30;
  }

  private boolean notHasNumber(String password) {
    // TODO: Pensar em um jeito melhor de fazer isso!
    // Talvez com RegExp
    for (int i = 0; i < 10; i++) {
      if (password.contains(String.valueOf(i))) {
        return false;
      }
    }
    return true;
  }

  private boolean hasWhiteSpace(String password) {
    if (password.contains(" ")) {
      return true;
    }
    return false;
  }
}
