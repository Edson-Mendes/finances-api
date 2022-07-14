package br.com.emendes.financesapi.service;

import br.com.emendes.financesapi.controller.dto.RoleDto;
import br.com.emendes.financesapi.controller.form.RoleForm;
import br.com.emendes.financesapi.model.Role;
import br.com.emendes.financesapi.repository.RoleRepository;
import br.com.emendes.financesapi.validation.exception.DataConflictException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

  @Autowired
  private RoleRepository roleRepository;

  public RoleDto create(RoleForm roleForm) {
    Role role = roleForm.toRole();
    try {
      roleRepository.save(role);
      return new RoleDto(role);
    } catch (DataIntegrityViolationException e) {
      throw new DataConflictException("já existe role com esse nome");
    }
  }

  public List<RoleDto> readAll() {
    List<Role> roles = roleRepository.findAll();
    return RoleDto.convert(roles);
  }

  public RoleDto readById(Long id) {
    Optional<Role> optionalRole = roleRepository.findById(id);

    return new RoleDto(optionalRole.orElseThrow(() -> {
      throw new NoResultException("não existe role com id: " + id);
    }));
  }

  public void deleteById(Long id) {
    try {
      roleRepository.deleteById(id);
    } catch (Exception e) {
      throw new NoResultException("não existe role com id: " + id);
    }
  }

}
