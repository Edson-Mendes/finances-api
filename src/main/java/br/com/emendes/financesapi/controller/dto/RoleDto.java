package br.com.emendes.financesapi.controller.dto;

import java.util.List;
import java.util.stream.Collectors;

import br.com.emendes.financesapi.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;

public class RoleDto {
  
  @Schema(example = "2")
  private Long id;

  @Schema(example = "ROLE_USER")
  private String name;

  public RoleDto(Role role) {
    this.id = role.getId();
    this.name = role.getName();
  }

  public RoleDto(Long id, String name){
    this.id = id;
    this.name = name;
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
