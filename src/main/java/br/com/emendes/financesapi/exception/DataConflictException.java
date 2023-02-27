package br.com.emendes.financesapi.exception;

public class DataConflictException extends RuntimeException {
  public DataConflictException(String message) {
    super(message);
  }
}
