package br.com.emendes.financesapi.controller.form;

import javax.validation.constraints.NotBlank;

import br.com.emendes.financesapi.model.Role;

public class RoleForm {
  
  // TODO: Adicionar uma validação para o nome do Role começar com 'ROLE'
  @NotBlank
  private String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Role toRole(){
    Role role = new Role(name);
    return role;
  }
}
