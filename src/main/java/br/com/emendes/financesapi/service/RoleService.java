package br.com.emendes.financesapi.service;

import java.util.List;
import java.util.Optional;

import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import br.com.emendes.financesapi.config.validation.exception.DataConflictException;
import br.com.emendes.financesapi.controller.dto.RoleDto;
import br.com.emendes.financesapi.controller.form.RoleForm;
import br.com.emendes.financesapi.model.Role;
import br.com.emendes.financesapi.repository.RoleRepository;

@Service
public class RoleService {

  @Autowired
  private RoleRepository roleRepository;

  public RoleDto create(RoleForm roleForm) {
    Role role = roleForm.toRole();
    try {
      roleRepository.save(role);
      RoleDto roleDto = new RoleDto(role);
      return roleDto;
      
    } catch (DataIntegrityViolationException e) {
      throw new DataConflictException("já existe role com esse nome");
    }
  }

  public List<RoleDto> readAll() {
    List<Role> roles = roleRepository.findAll();
    List<RoleDto> rolesDto = RoleDto.convert(roles);

    return rolesDto;
  }

  public RoleDto readById(Long id) {
    Optional<Role> optionalRole = roleRepository.findById(id);
    if (optionalRole.isEmpty()) {
      throw new NoResultException("não existe role com id: " + id);
    }
    return new RoleDto(optionalRole.get());
  }

  public void deleteById(Long id) {
    try {
      roleRepository.deleteById(id);
    } catch (Exception e) {
      throw new NoResultException("não existe role com id: " + id);
    }
  }

}
