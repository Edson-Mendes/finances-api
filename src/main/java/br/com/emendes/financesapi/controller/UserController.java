package br.com.emendes.financesapi.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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

@RestController
@RequestMapping("/user")
public class UserController {
  
  @Autowired
  private UserService userService;

  @Autowired TokenService tokenService;

  @GetMapping
  public ResponseEntity<List<UserDto>> read(){
    return userService.read();
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id){
    // FIXME: Erro 500 ao tentar deletar usuário
    // TODO: Deletar todos os registros que pertenciam ao usuário?
    userService.delete(id);
  }

  @PutMapping("/change-password")
  @Transactional
  public void changePassword(@Valid @RequestBody ChangePasswordForm changeForm, HttpServletRequest request){
    Long userId = tokenService.getUserId(request);

    userService.changePassword(changeForm, userId);
  }

}
