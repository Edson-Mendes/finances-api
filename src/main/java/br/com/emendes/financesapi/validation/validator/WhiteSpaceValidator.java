package br.com.emendes.financesapi.validation.validator;

import br.com.emendes.financesapi.validation.annotation.NoWhiteSpace;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class WhiteSpaceValidator implements ConstraintValidator<NoWhiteSpace, String> {

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) return true;

    return !value.contains(" ") && !value.contains("\n") && !value.contains("\t");
  }

}
