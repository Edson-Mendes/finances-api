package br.com.emendes.financesapi.validation.exception;

public class DataConflictException extends RuntimeException {
  public DataConflictException(String message) {
    super(message);
  }
}
