package br.com.emendes.financesapi.service;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.emendes.financesapi.config.validation.error_dto.ErrorDto;
import br.com.emendes.financesapi.controller.dto.RoleDto;
import br.com.emendes.financesapi.controller.form.RoleForm;
import br.com.emendes.financesapi.model.Role;
import br.com.emendes.financesapi.repository.RoleRepository;

@Service
public class RoleService {

  @Autowired
  private RoleRepository roleRepository;

  public ResponseEntity<?> create(RoleForm roleForm, UriComponentsBuilder uriBuilder) {
    Role role = roleForm.toRole();
    try {
      roleRepository.save(role);
      RoleDto roleDto = new RoleDto(role);
      URI uri = uriBuilder.path("/role/{id}").buildAndExpand(role.getId()).toUri();
      return ResponseEntity.created(uri).body(roleDto);
    } catch (DataIntegrityViolationException e) {
      ErrorDto errorDto = new ErrorDto("CONFLICT", "já existe role com esse nome");
      return ResponseEntity.status(HttpStatus.CONFLICT).header("Content-Type", "application/json;charset=UTF-8")
          .body(errorDto);
    }
  }

  public ResponseEntity<List<RoleDto>> readAll() {
    List<Role> roles = roleRepository.findAll();
    List<RoleDto> rolesDto = RoleDto.convert(roles);

    return ResponseEntity.ok(rolesDto);
  }

  public ResponseEntity<?> readById(Long id) {
    Optional<Role> optionalRole = roleRepository.findById(id);
    if (optionalRole.isPresent()) {
      return ResponseEntity
          .status(HttpStatus.OK)
          .header("Content-Type", "application/json;charset=UTF-8")
          .body(new RoleDto(optionalRole.get()));
    }
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .header("Content-Type", "application/json;charset=UTF-8")
        .body(new ErrorDto("Not Found", "não existe role com id: " + id));
  }

  public ResponseEntity<?> deleteById(Long id) {
    try {
      roleRepository.deleteById(id);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      return ResponseEntity
          .status(HttpStatus.NOT_FOUND)
          .header("Content-Type", "application/json;charset=UTF-8")
          .body(new ErrorDto("Not Found", "não existe role com id: " + id));
    }
  }

}
