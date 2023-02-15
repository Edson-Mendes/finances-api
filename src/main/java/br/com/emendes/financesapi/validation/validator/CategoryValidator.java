package br.com.emendes.financesapi.validation.validator;

import br.com.emendes.financesapi.model.Category;
import br.com.emendes.financesapi.validation.annotation.CategoryValidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CategoryValidator implements ConstraintValidator<CategoryValidation, String> {

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null)
      return true;

    try {
      Category.valueOf(value);
      return true;
    } catch (IllegalArgumentException ex) {
      return false;
    }
  }

}
