package br.com.emendes.financesapi.service.impl;

import br.com.emendes.financesapi.dto.request.ChangePasswordRequest;
import br.com.emendes.financesapi.dto.request.SignupRequest;
import br.com.emendes.financesapi.dto.response.UserResponse;
import br.com.emendes.financesapi.exception.DataConflictException;
import br.com.emendes.financesapi.exception.EntityNotFoundException;
import br.com.emendes.financesapi.exception.PasswordsDoNotMatchException;
import br.com.emendes.financesapi.exception.WrongPasswordException;
import br.com.emendes.financesapi.model.entity.User;
import br.com.emendes.financesapi.repository.UserRepository;
import br.com.emendes.financesapi.service.UserService;
import br.com.emendes.financesapi.util.component.CurrentAuthenticationComponent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Implementação de {@link UserService}.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final CurrentAuthenticationComponent currentAuthenticationComponent;

  @Override
  public UserResponse createAccount(SignupRequest signupRequest) {
    log.info("attempt to create account");
    if (passwordsNotMatch(signupRequest.getPassword(), signupRequest.getConfirm())) {
      throw new PasswordsDoNotMatchException("Password and confirm does not match");
    }
    try {
      User user = signupRequest.toUser();
      user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
      return new UserResponse(userRepository.save(user));
    } catch (DataIntegrityViolationException e) {
      throw new DataConflictException("Email is already in use");
    }
  }

  @Override
  public Page<UserResponse> read(Pageable pageable) {
    log.info("attempt to read users");
    Page<User> users = userRepository.findAll(pageable);
    return UserResponse.convert(users);
  }

  @Override
  public User readById(Long userId) {
    log.info("attempt to read user with id: {}", userId);
    return userRepository.findById(userId).orElse(null);
  }

  @Override
  public void delete(Long id) {
    log.info("attempt to delete user with id: {}", id);
    User user = userRepository.findById(id).orElseThrow(() -> {
      log.info("user not found with id: {}", id);
      return new EntityNotFoundException("User not found with id " + id);
    });

    userRepository.delete(user);
    log.info("user deleted successfully with id: {}", id);
  }

  @Override
  @Transactional
  public void changePassword(ChangePasswordRequest changeRequest) {
    log.info("attempt to change password");
    if (passwordsNotMatch(changeRequest.getNewPassword(), changeRequest.getConfirm())) {
      throw new PasswordsDoNotMatchException("Passwords do not match");
    }

    User currentUser = currentAuthenticationComponent.getCurrentUser();
    if (!passwordEncoder.matches(changeRequest.getOldPassword(), currentUser.getPassword())) {
      throw new WrongPasswordException("Wrong password");
    }

    currentUser.setPassword(passwordEncoder.encode(changeRequest.getNewPassword()));
    userRepository.save(currentUser);
  }

  /**
   * Verifica se {@code password} corresponde a {@code confirPassword}.
   *
   * @param password        senha a ser verificada
   * @param confirmPassword confirmação de senha para comparação.
   * @return {@code true} caso correspondam, {@code false} caso contrário.
   */
  private boolean passwordsNotMatch(String password, String confirmPassword) {
    if (password == null || confirmPassword == null) {
      throw new IllegalArgumentException("password and confirmPassword must not be null");
    }
    return !password.equals(confirmPassword);
  }

}
