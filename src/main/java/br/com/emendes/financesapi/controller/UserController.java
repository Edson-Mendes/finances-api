package br.com.emendes.financesapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.emendes.financesapi.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
  
  @Autowired
  private UserService userService;

  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete(@PathVariable Long id){
    return userService.delete(id);
  }

}
