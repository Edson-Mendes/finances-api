package br.com.emendes.financesapi.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.com.emendes.financesapi.validation.validator.DateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * The {@code String} must be a {@code String} that can be parse to {@code LocalDate}.
 * Accepts {@code String} and {@code CharSequence}.
 * <p>
 * {@code null} elements are considered valid.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateValidator.class)
public @interface DateValidation {
  String message() default "Invalid date";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  String value() default "";
}
