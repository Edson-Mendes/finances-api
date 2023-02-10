package br.com.emendes.financesapi.controller;

import br.com.emendes.financesapi.dto.response.UserResponse;
import br.com.emendes.financesapi.controller.form.ChangePasswordForm;
import br.com.emendes.financesapi.controller.openapi.UserControllerOpenAPI;
import br.com.emendes.financesapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController implements UserControllerOpenAPI {

  private final UserService userService;

  @Override
  @GetMapping
  public ResponseEntity<Page<UserResponse>> readAll(
      @ParameterObject @PageableDefault(sort = "id", direction = Direction.ASC) Pageable pageable) {
    Page<UserResponse> usersDto = userService.read(pageable);
    return ResponseEntity.ok(usersDto);
  }

  @Override
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    userService.delete(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Override
  @PutMapping("/password")
  @Transactional
  public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordForm changeForm) {
    userService.changePassword(changeForm);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
