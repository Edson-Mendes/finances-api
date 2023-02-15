package br.com.emendes.financesapi.validation.annotation;

import br.com.emendes.financesapi.validation.validator.CategoryValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The {@code String} must be a valid {@code Category}.
 * Accepts only {@code String}.
 * <p>
 * {@code null} elements are considered valid.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CategoryValidator.class)
public @interface CategoryValidation {

  String message() default "invalid category";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

}
