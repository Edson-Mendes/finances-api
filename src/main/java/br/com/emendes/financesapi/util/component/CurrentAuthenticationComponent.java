package br.com.emendes.financesapi.util.component;

import br.com.emendes.financesapi.exception.UserIsNotAuthenticatedException;
import br.com.emendes.financesapi.model.entity.User;

/**
 * Interface com as abstrações responsáveis por manipular o usuário da requisição atual.
 */
public interface CurrentAuthenticationComponent {

  /**
   * Busca o usuário atual autenticado.
   *
   * @return O usuário (User) atual autenticado.
   * @throws UserIsNotAuthenticatedException caso não tenha usuário autenticado no contexto atual.
   */
  User getCurrentUser();

}
