package br.com.emendes.financesapi.validation.validator;

import br.com.emendes.financesapi.validation.annotation.DateValidation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class DateValidator implements ConstraintValidator<DateValidation, String> {

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    try {
      LocalDate.parse(value);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

}
