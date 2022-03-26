package br.com.emendes.financesapi.config.validation.exception;

public class PasswordsDoNotMatchException extends RuntimeException{

  public PasswordsDoNotMatchException(String message) {
    super(message);
  }

}
