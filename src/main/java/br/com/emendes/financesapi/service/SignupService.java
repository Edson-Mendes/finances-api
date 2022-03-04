package br.com.emendes.financesapi.service;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.emendes.financesapi.config.validation.error_dto.ErrorDto;
import br.com.emendes.financesapi.controller.dto.UserDto;
import br.com.emendes.financesapi.controller.form.SignupForm;
import br.com.emendes.financesapi.model.User;
import br.com.emendes.financesapi.repository.UserRepository;

@Service
public class SignupService {

  @Autowired
  private UserRepository userRepository;

  public ResponseEntity<?> createAccount(SignupForm signupForm, UriComponentsBuilder uriBuilder) {
    if (signupForm.isMatch()) {
      User newUser = signupForm.toUser();
      try {
        userRepository.save(newUser);
        URI uri = uriBuilder.path("/user/{id}").buildAndExpand(newUser.getId()).toUri();
        return ResponseEntity.created(uri).body(new UserDto(newUser));
      } catch (DataIntegrityViolationException e) {
        ErrorDto errorDto = new ErrorDto("CONFLICT", "Email inserido já está em uso!");
        return ResponseEntity.status(HttpStatus.CONFLICT).header("Content-Type", "application/json;charset=UTF-8")
            .body(errorDto);
      }
    }

    return ResponseEntity.badRequest().header("Content-Type", "application/json;charset=UTF-8")
        .body(new ErrorDto("Bad Request", "As senhas não correspondem!"));

  }

}
