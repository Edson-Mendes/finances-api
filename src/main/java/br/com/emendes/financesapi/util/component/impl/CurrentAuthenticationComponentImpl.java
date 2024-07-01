package br.com.emendes.financesapi.util.component.impl;

import br.com.emendes.financesapi.exception.UserIsNotAuthenticatedException;
import br.com.emendes.financesapi.model.entity.User;
import br.com.emendes.financesapi.util.component.CurrentAuthenticationComponent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Implementação de {@link CurrentAuthenticationComponent}.
 */
@Component
public class CurrentAuthenticationComponentImpl implements CurrentAuthenticationComponent {

  @Override
  public User getCurrentUser() {
    Authentication authentication = getAuthentication();
    if (authentication == null ||
        !authentication.isAuthenticated() ||
        !(authentication.getPrincipal() instanceof User)) {
      throw new UserIsNotAuthenticatedException("User is not authenticate");
    }
    return (User) authentication.getPrincipal();
  }

  /**
   * Retorna o {@link Authentication} do contexto de segurança.
   *
   * @return Objeto Authentication do contexto de segurança atual.
   */
  private Authentication getAuthentication() {
    return SecurityContextHolder.getContext().getAuthentication();
  }

}
