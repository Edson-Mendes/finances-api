package br.com.emendes.financesapi.creator;

import br.com.emendes.financesapi.model.Role;

public class RoleCreator {

  public static Role userRole() {
    return new Role(1l, "ROLE_USER");
  }

}
