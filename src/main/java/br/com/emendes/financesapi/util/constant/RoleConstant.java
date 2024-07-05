package br.com.emendes.financesapi.util.constant;

import br.com.emendes.financesapi.model.entity.Role;

/**
 * Classe com constantes relacionadas a Role.
 */
public final class RoleConstant {

  private RoleConstant() {
  }

  public static final String ROLE_ADMIN = "ADMIN";

  public static final Role USER_ROLE = Role.builder()
      .id(1)
      .name("ROLE_USER")
      .build();

}
