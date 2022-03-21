package br.com.emendes.financesapi.controller;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.emendes.financesapi.config.security.TokenService;
import br.com.emendes.financesapi.controller.form.ChangePasswordForm;
import br.com.emendes.financesapi.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
  
  @Autowired
  private UserService userService;

  @Autowired TokenService tokenService;

  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete(@PathVariable Long id){
    return userService.delete(id);
  }

  @PutMapping("/change-password")
  @Transactional
  public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordForm changeForm, HttpServletRequest request){
    String token = tokenService.recoverToken(request);
    Long userId = tokenService.getIdUser(token);

    return userService.changePassword(changeForm, userId);
  }

}
