package br.com.emendes.financesapi.service;

import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.emendes.financesapi.config.validation.exception.DataConflictException;
import br.com.emendes.financesapi.config.validation.exception.PasswordsDoNotMatchException;
import br.com.emendes.financesapi.controller.dto.UserDto;
import br.com.emendes.financesapi.controller.form.ChangePasswordForm;
import br.com.emendes.financesapi.controller.form.SignupForm;
import br.com.emendes.financesapi.model.User;
import br.com.emendes.financesapi.repository.UserRepository;

@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  public UserDto createAccount(SignupForm signupForm) {
    if (signupForm.passwordMatch()) {
      User user = signupForm.toUser();
      try {
        userRepository.save(user);
        return new UserDto(user);
        
      } catch (DataIntegrityViolationException e) {
        throw new DataConflictException("Email inserido já está em uso!");
      }
    }
    throw new PasswordsDoNotMatchException("As senhas não correspondem!");
  }

  public Page<UserDto> read(Pageable pageable) {
    Page<User> users = userRepository.findAll(pageable);
    Page<UserDto> usersDto = UserDto.convert(users);
    return usersDto;
  }

  public User readById(Long userId){
    return userRepository.findById(userId).orElse(null);
  }

  public void delete(Long id) {
    try {
      userRepository.deleteById(id);
    } catch (EmptyResultDataAccessException e) {
      throw new NoResultException("não existe usuário com id: " + id);
    }
  }

  public void changePassword(ChangePasswordForm changeForm, Long userId) {
    if (changeForm.passwordMatch()) {
      User user = userRepository.findById(userId).get();
      user.setPassword(changeForm.generateNewPasswordEncoded());
    } else {
      throw new PasswordsDoNotMatchException("as senhas não correspondem!");
    }
  }
}
