package br.com.emendes.financesapi.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import br.com.emendes.financesapi.validation.validator.DateValidator;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateValidator.class)
public @interface DateValidation {
  String message() default "A data informada é inválida!";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  String value() default "";
}
