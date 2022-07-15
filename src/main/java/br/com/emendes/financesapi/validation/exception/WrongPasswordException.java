package br.com.emendes.financesapi.validation.exception;

public class WrongPasswordException extends RuntimeException{

  public WrongPasswordException(String message){
    super(message);
  }

}
