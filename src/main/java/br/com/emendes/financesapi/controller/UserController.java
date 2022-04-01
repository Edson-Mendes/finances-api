package br.com.emendes.financesapi.controller;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.emendes.financesapi.config.security.TokenService;
import br.com.emendes.financesapi.controller.dto.UserDto;
import br.com.emendes.financesapi.controller.form.ChangePasswordForm;
import br.com.emendes.financesapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/user")
public class UserController {
  
  @Autowired
  private UserService userService;

  @Autowired TokenService tokenService;

  @Operation(security = { @SecurityRequirement(name = "bearer-key") })
  @GetMapping
  public ResponseEntity<Page<UserDto>> read(
      @PageableDefault(sort = "id", direction = Direction.ASC, page = 0, size = 10) Pageable pageable){
    return userService.read(pageable);
  }

  @Operation(security = { @SecurityRequirement(name = "bearer-key") })
  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id){
    userService.delete(id);
  }

  @Operation(security = { @SecurityRequirement(name = "bearer-key") })
  @PutMapping("/change-password")
  @Transactional
  public void changePassword(@Valid @RequestBody ChangePasswordForm changeForm, HttpServletRequest request){
    Long userId = tokenService.getUserId(request);

    userService.changePassword(changeForm, userId);
  }

}
