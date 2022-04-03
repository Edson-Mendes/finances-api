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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/role")
public class RoleController {

  @Autowired
  private RoleService roleService;

  @Operation(summary = "Buscar roles", security = { @SecurityRequirement(name = "bearer-key") })
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Encontrou os roles"),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(responseCode = "403", description = "Forbidden, usuário não tem permissão de acesso", content = @Content),
  })
  @GetMapping
  public ResponseEntity<List<RoleDto>> readAll() {
    return roleService.readAll();
  }

  @Operation(summary = "Buscar role por id", security = { @SecurityRequirement(name = "bearer-key") })
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Encontrou o role"),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(responseCode = "403", description = "Forbidden, usuário não tem permissão de acesso", content = @Content),
    @ApiResponse(responseCode = "404", description = "Role não encontrado", content = @Content),
  })
  @GetMapping("/{id}")
  public ResponseEntity<RoleDto> readById(@PathVariable Long id) {
    return roleService.readById(id);
  }

}
