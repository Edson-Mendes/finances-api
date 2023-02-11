package br.com.emendes.financesapi.validation.annotation;

import br.com.emendes.financesapi.validation.validator.WhiteSpaceValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The string must not contain whitespace, tab or newline.
 * Accepts only {@code String}.
 * <p>
 * {@code null} elements are considered valid.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = WhiteSpaceValidator.class)
public @interface NoWhiteSpace {

  String message() default "must not contain whitespace";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

}
