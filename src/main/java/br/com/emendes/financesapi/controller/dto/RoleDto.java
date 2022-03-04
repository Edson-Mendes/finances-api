package br.com.emendes.financesapi.controller.dto;

import java.util.List;
import java.util.stream.Collectors;

import br.com.emendes.financesapi.model.Role;

public class RoleDto {
  private Long id;
  private String name;

  public RoleDto(Role role) {
    this.id = role.getId();
    this.name = role.getName();
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public static List<RoleDto> convert(List<Role> roles) {
    List<RoleDto> rolesDto = roles.stream().map(RoleDto::new).collect(Collectors.toList());
    return rolesDto;
  }
}
