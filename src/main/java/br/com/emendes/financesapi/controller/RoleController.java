package br.com.emendes.financesapi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.emendes.financesapi.controller.dto.RoleDto;
import br.com.emendes.financesapi.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/role")
public class RoleController {

  @Autowired
  private RoleService roleService;

  @Operation(security = { @SecurityRequirement(name = "bearer-key") })
  @GetMapping
  public ResponseEntity<List<RoleDto>> readAll() {
    return roleService.readAll();
  }

  @Operation(security = { @SecurityRequirement(name = "bearer-key") })
  @GetMapping("/{id}")
  public ResponseEntity<RoleDto> readById(@PathVariable Long id) {
    return roleService.readById(id);
  }

}
