package br.com.emendes.financesapi.controller;

import br.com.emendes.financesapi.controller.openapi.UserControllerOpenAPI;
import br.com.emendes.financesapi.dto.request.ChangePasswordRequest;
import br.com.emendes.financesapi.dto.response.UserResponse;
import br.com.emendes.financesapi.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/users", produces = "application/json;charset=UTF-8")
public class UserController implements UserControllerOpenAPI {

  private final UserService userService;

  @Override
  @GetMapping
  public ResponseEntity<Page<UserResponse>> readAll(
      @PageableDefault(sort = "id", direction = Direction.ASC) Pageable pageable) {
    return ResponseEntity.ok(userService.read(pageable));
  }

  @Override
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    userService.delete(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Override
  @PutMapping("/password")
  public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest changeRequest) {
    userService.changePassword(changeRequest);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
