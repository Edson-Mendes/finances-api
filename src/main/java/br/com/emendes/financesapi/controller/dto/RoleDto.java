package br.com.emendes.financesapi.controller.dto;

import br.com.emendes.financesapi.model.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Objects;

public class RoleDto {
  
  @Schema(example = "2")
  private Integer id;

  @Schema(example = "ROLE_USER")
  private String name;

  public RoleDto(Role role) {
    this.id = role.getId();
    this.name = role.getName();
  }

  public RoleDto(Integer id, String name){
    this.id = id;
    this.name = name;
  }

  public Integer getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public static List<RoleDto> convert(List<Role> roles) {
    return roles.stream().map(RoleDto::new).toList();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RoleDto roleDto = (RoleDto) o;
    return Objects.equals(id, roleDto.id) && Objects.equals(name, roleDto.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name);
  }
}
