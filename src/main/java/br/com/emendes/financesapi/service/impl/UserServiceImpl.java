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
import br.com.emendes.financesapi.util.AuthenticationFacade;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationFacade authenticationFacade;

  @Override
  public UserResponse createAccount(SignupRequest signupRequest) {
    if (signupRequest.passwordMatch()) {
      User user = signupRequest.toUser();
      user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
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
    User currentUser = (User) authenticationFacade.getAuthentication().getPrincipal();
    if (passwordsMatch(changeRequest.getOldPassword(), currentUser.getPassword())) {
      if (changeRequest.passwordMatch()) {
        currentUser.setPassword(passwordEncoder.encode(changeRequest.getNewPassword()));
        userRepository.save(currentUser);
        return;
      }
      throw new PasswordsDoNotMatchException("Passwords do not match");
    }
    throw new WrongPasswordException("Wrong password");
  }

  private boolean passwordsMatch(String password, String encodedPassword) {
    return passwordEncoder.matches(password, encodedPassword);
  }

}
