package br.com.emendes.financesapi.util.creator;

import br.com.emendes.financesapi.model.entity.Role;

public class RoleCreator {

  public static Role userRole() {
    return new Role(1, "ROLE_USER");
  }

}
