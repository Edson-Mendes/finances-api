package br.com.emendes.financesapi.service;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.com.emendes.financesapi.config.validation.error_dto.ErrorDto;
import br.com.emendes.financesapi.controller.form.ChangePasswordForm;
import br.com.emendes.financesapi.model.User;
import br.com.emendes.financesapi.repository.UserRepository;

@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  public ResponseEntity<?> delete(Long id) {
    try {
      userRepository.deleteById(id);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      return ResponseEntity
          .status(HttpStatus.NOT_FOUND)
          .header("Content-Type", "application/json;charset=UTF-8")
          .body(new ErrorDto("Not Found", "não existe usuário com id: " + id));
    }
  }

  public ResponseEntity<?> changePassword(@Valid ChangePasswordForm changeForm, Long userId) {
    if (changeForm.isMatch()) {
      User user = userRepository.findById(userId).get();
      user.setPassword(changeForm.getNewPasswordEncoded());

      return ResponseEntity.ok().build();
    }
    return ResponseEntity.badRequest().header("Content-Type", "application/json;charset=UTF-8")
        .body(new ErrorDto("Bad Request", "as senhas não correspondem!"));
  }

}
