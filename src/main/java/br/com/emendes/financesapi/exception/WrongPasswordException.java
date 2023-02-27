package br.com.emendes.financesapi.exception;

public class WrongPasswordException extends RuntimeException{

  public WrongPasswordException(String message){
    super(message);
  }

}
