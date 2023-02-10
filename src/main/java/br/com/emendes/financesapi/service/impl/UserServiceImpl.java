package br.com.emendes.financesapi.service.impl;

import br.com.emendes.financesapi.controller.form.ChangePasswordForm;
import br.com.emendes.financesapi.dto.request.SignupRequest;
import br.com.emendes.financesapi.dto.response.UserResponse;
import br.com.emendes.financesapi.model.entity.User;
import br.com.emendes.financesapi.repository.UserRepository;
import br.com.emendes.financesapi.service.UserService;
import br.com.emendes.financesapi.validation.exception.DataConflictException;
import br.com.emendes.financesapi.validation.exception.PasswordsDoNotMatchException;
import br.com.emendes.financesapi.validation.exception.WrongPasswordException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  @Override
  public UserResponse createAccount(SignupRequest signupRequest) {
    if (signupRequest.passwordMatch()) {
      User user = signupRequest.toUser();
      try {
        return new UserResponse(userRepository.save(user));
      } catch (DataIntegrityViolationException e) {
        throw new DataConflictException("Email is already in use");
      }
    }
    throw new PasswordsDoNotMatchException("Password and confirm do not match");
  }

  @Override
  public Page<UserResponse> read(Pageable pageable) {
    Page<User> users = userRepository.findAll(pageable);
    return UserResponse.convert(users);
  }

  @Override
  public User readById(Long userId) {
    return userRepository.findById(userId).orElse(null);
  }

  @Override
  public void delete(Long id) {
    try {
      userRepository.deleteById(id);
    } catch (EmptyResultDataAccessException e) {
      throw new NoResultException("não existe usuário com id: " + id);
    }
  }

  @Override
  public void changePassword(ChangePasswordForm changeForm) {
    if (changeForm.passwordMatch()) {
      User user = userRepository.findCurrentUser().orElseThrow(
          () -> new NoResultException("Não foi possível encontrar o usuário atual"));
      if (passwordsMatch(changeForm.getOldPassword(), user.getPassword())) {
        user.setPassword(changeForm.generateNewPasswordEncoded());
      } else {
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
