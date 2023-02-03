package br.com.emendes.financesapi.controller.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import br.com.emendes.financesapi.model.entity.Role;

public class RoleForm {

  @NotBlank
  @Pattern(regexp = "ROLE_[A-Z]{1,}", message = "deve iniciar com \'ROLE_\' e continuar em letras maiúsculas")
  private String name;

  public RoleForm() {
  }

  public RoleForm(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Role toRole() {
    Role role = new Role(name);
    return role;
  }
}
