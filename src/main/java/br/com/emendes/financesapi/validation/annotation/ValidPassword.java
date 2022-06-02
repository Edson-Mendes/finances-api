package br.com.emendes.financesapi.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import br.com.emendes.financesapi.validation.validator.PasswordValidator;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidator.class)
public @interface ValidPassword {
  String message() default "Senha inválida";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
