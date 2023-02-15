package br.com.emendes.financesapi.service;

import br.com.emendes.financesapi.dto.response.UserResponse;
import br.com.emendes.financesapi.dto.request.ChangePasswordRequest;
import br.com.emendes.financesapi.dto.request.SignupRequest;
import br.com.emendes.financesapi.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

  UserResponse createAccount(SignupRequest signupRequest);

  Page<UserResponse> read(Pageable pageable);

  User readById(Long userId);

  void delete(Long id);

  void changePassword(ChangePasswordRequest changeForm);

}
