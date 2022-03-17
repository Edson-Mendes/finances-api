package br.com.emendes.financesapi.config.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidator.class)
public @interface ValidPassword {
  // TODO: Como substituir a message default?
    String message() default "Senha inválida";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

  @Override
  public boolean isValid(String password, ConstraintValidatorContext context) {
    if(isNotSizeEnough(password)){
      context.buildConstraintViolationWithTemplate("deve ter de 8 a 30 caracteres.")
          .addConstraintViolation();
      return false;
    }
    if(notHasNumber(password)){
      context.buildConstraintViolationWithTemplate("deve ter pelo menos um número.")
          .addConstraintViolation();
      return false;
    }
    if(hasWhiteSpace(password)){
      context.buildConstraintViolationWithTemplate("não deve ter espaço em branco.")
          .addConstraintViolation();
      return false;
    }
    return true;
  }

  private boolean isNotSizeEnough(String password){
    return password.length() < 8 || password.length() > 30;
  }

  private boolean notHasNumber(String password){
    // TODO: Pensar em um jeito melhor de fazer isso!
    // Talvez com RegExp
    for(int i = 0; i < 10; i++){
      if(password.contains(String.valueOf(i))){
        return false;
      }
    }
    return true;
  }

  private boolean hasWhiteSpace(String password){
    if(password.contains(" ")){
      return true;
    }
    return false;
  }
}
