package br.com.emendes.financesapi.service;

import br.com.emendes.financesapi.controller.dto.UserDto;
import br.com.emendes.financesapi.controller.form.ChangePasswordForm;
import br.com.emendes.financesapi.controller.form.SignupForm;
import br.com.emendes.financesapi.model.User;
import br.com.emendes.financesapi.repository.UserRepository;
import br.com.emendes.financesapi.validation.exception.DataConflictException;
import br.com.emendes.financesapi.validation.exception.PasswordsDoNotMatchException;
import br.com.emendes.financesapi.validation.exception.WrongPasswordException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;

@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  public UserDto createAccount(SignupForm signupForm) {
    if (signupForm.passwordMatch()) {
      User user = signupForm.toUser();
      try {
        return new UserDto(userRepository.save(user));
      } catch (DataIntegrityViolationException e) {
        throw new DataConflictException("Email inserido já está em uso!");
      }
    }
    throw new PasswordsDoNotMatchException("As senhas não correspondem!");
  }

  public Page<UserDto> read(Pageable pageable) {
    Page<User> users = userRepository.findAll(pageable);
    return UserDto.convert(users);
  }

  public User readById(Long userId) {
    return userRepository.findById(userId).orElse(null);
  }

  public void delete(Long id) {
    try {
      userRepository.deleteById(id);
    } catch (EmptyResultDataAccessException e) {
      throw new NoResultException("não existe usuário com id: " + id);
    }
  }

  public void changePassword(ChangePasswordForm changeForm) {
    if (changeForm.passwordMatch()) {
      User user = userRepository.findCurrentUser().orElseThrow(
          () -> new NoResultException("Não foi possível encontrar o usuário atual"));
      if (passwordsMatch(changeForm.getOldPassword(), user.getPassword())) {
        user.setPassword(changeForm.generateNewPasswordEncoded());
      }else{
        throw new WrongPasswordException("Senha incorreta");
      }
    } else {
      throw new PasswordsDoNotMatchException("as senhas não correspondem!");
    }
  }

  private boolean passwordsMatch(String password, String encodedPassword) {
    return new BCryptPasswordEncoder().matches(password, encodedPassword);
  }

}
