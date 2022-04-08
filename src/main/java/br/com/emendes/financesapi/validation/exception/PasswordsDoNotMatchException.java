package br.com.emendes.financesapi.validation.exception;

public class PasswordsDoNotMatchException extends RuntimeException {

  public PasswordsDoNotMatchException(String message) {
    super(message);
  }

}
