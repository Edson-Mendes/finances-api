package br.com.emendes.financesapi.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.emendes.financesapi.controller.dto.RoleDto;
import br.com.emendes.financesapi.controller.form.RoleForm;
import br.com.emendes.financesapi.service.RoleService;

@RestController
@RequestMapping("/role")
public class RoleController {

  @Autowired
  private RoleService roleService;

  @PostMapping
  public ResponseEntity<RoleDto> create(@RequestBody @Valid RoleForm roleForm, UriComponentsBuilder uriBuilder) {
    return roleService.create(roleForm, uriBuilder);
  }

  @GetMapping
  public ResponseEntity<List<RoleDto>> readAll() {
    return roleService.readAll();
  }

  @GetMapping("/{id}")
  public ResponseEntity<RoleDto> readById(@PathVariable Long id){
    return roleService.readById(id);
  }

  @DeleteMapping("/{id}")
  public void deleteById(@PathVariable Long id){
    roleService.deleteById(id);
  }

}
