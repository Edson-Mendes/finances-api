package br.com.emendes.financesapi.service;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.emendes.financesapi.controller.dto.RoleDto;
import br.com.emendes.financesapi.controller.form.RoleForm;
import br.com.emendes.financesapi.model.Role;
import br.com.emendes.financesapi.repository.RoleRepository;

@Service
public class RoleService {

  @Autowired
  private RoleRepository roleRepository;

  public ResponseEntity<RoleDto> create(RoleForm roleForm, UriComponentsBuilder uriBuilder) {
    Role role = roleForm.toRole();
    roleRepository.save(role);
    RoleDto roleDto = new RoleDto(role);
    URI uri = uriBuilder.path("/role/{id}").buildAndExpand(role.getId()).toUri();
    return ResponseEntity.created(uri).body(roleDto);
  }

  public ResponseEntity<List<RoleDto>> readAll() {
    // TODO: Buscar lista de Roles no repositório.
    List<Role> roles = roleRepository.findAll();
    List<RoleDto> rolesDto = RoleDto.convert(roles);

    return ResponseEntity.ok(rolesDto);
  }

}
