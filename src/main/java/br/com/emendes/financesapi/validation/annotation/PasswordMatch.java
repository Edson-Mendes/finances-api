package br.com.emendes.financesapi.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import br.com.emendes.financesapi.dto.request.SignupRequest;

// TODO: Pensar em como fazer valer para os campos de password e confirm.
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PassValidator.class)
public @interface PasswordMatch {

  String message() default "As senhas não correspondem";

  String confirm() default "";

  // Class<?>[] groups() default {};

  // Class<? extends Payload>[] payload() default {};
}

class PassValidator implements ConstraintValidator<PasswordMatch, SignupRequest> {

  @Override
  public boolean isValid(SignupRequest signForm, ConstraintValidatorContext context) {
    String pass = signForm.getPassword();
    String confirm = signForm.getConfirm();

    if (pass.equals(confirm)) {
      return true;
    }

    return false;
  }

}
