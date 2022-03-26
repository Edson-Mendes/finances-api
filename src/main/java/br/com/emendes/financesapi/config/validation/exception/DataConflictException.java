package br.com.emendes.financesapi.config.validation.exception;

public class DataConflictException extends RuntimeException{
  public DataConflictException(String message){
    super(message);
  }
}
