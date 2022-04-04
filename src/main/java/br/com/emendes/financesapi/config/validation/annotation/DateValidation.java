package br.com.emendes.financesapi.config.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.LocalDate;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

import br.com.emendes.financesapi.util.Formatter;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateValidator.class)
public @interface DateValidation {
  String message() default "A data informada é inválida!";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  String value() default "";
}

class DateValidator implements ConstraintValidator<DateValidation, String>{

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    try{
      LocalDate.parse(value, Formatter.dateFormatter);
      return true;
    }catch(Exception e){
      return false;
    }
  }
  
}
