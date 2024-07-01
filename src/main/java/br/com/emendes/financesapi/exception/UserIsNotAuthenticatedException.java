package br.com.emendes.financesapi.exception;

/**
 * Exception a ser lançada quando não há usuário logado no contexto atual.
 */
public class UserIsNotAuthenticatedException extends RuntimeException {

  public UserIsNotAuthenticatedException(String message) {
    super(message);
  }

}
