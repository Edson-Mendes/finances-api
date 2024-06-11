package br.com.emendes.financesapi.util.faker;

import br.com.emendes.financesapi.model.entity.Role;

/**
 * Classe com objetos relacionados a Role para serem usados em testes automatizados.
 */
public class RoleFaker {

  /**
   * Retorna uma Role com todos os dados e nome igual USER_ROLE.
   */
  public static Role userRole() {
    return Role.builder()
        .id(1)
        .name("USER_ROLE")
        .build();
  }

}
