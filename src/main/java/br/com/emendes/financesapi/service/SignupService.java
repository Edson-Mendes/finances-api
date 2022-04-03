package br.com.emendes.financesapi.service;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.emendes.financesapi.config.validation.exception.DataConflictException;
import br.com.emendes.financesapi.config.validation.exception.PasswordsDoNotMatchException;
import br.com.emendes.financesapi.controller.dto.UserDto;
import br.com.emendes.financesapi.controller.form.SignupForm;
import br.com.emendes.financesapi.model.User;
import br.com.emendes.financesapi.repository.UserRepository;

@Service
public class SignupService {

  @Autowired
  private UserRepository userRepository;

  public ResponseEntity<UserDto> createAccount(SignupForm signupForm, UriComponentsBuilder uriBuilder) {
    if (signupForm.passwordMatch()) {
      User newUser = signupForm.toUser();
      try {
        userRepository.save(newUser);
        URI uri = uriBuilder.path("/user/{id}").buildAndExpand(newUser.getId()).toUri();
        return ResponseEntity.created(uri).body(new UserDto(newUser));
      } catch (DataIntegrityViolationException e) {
        throw new DataConflictException("Email inserido já está em uso!");
      }
    }
    throw new PasswordsDoNotMatchException("As senhas não correspondem!");
  }

}
