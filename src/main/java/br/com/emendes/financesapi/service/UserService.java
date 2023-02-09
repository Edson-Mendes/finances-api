package br.com.emendes.financesapi.service;

import br.com.emendes.financesapi.controller.dto.UserDto;
import br.com.emendes.financesapi.controller.form.ChangePasswordForm;
import br.com.emendes.financesapi.controller.form.SignupForm;
import br.com.emendes.financesapi.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

  UserDto createAccount(SignupForm signupForm);

  Page<UserDto> read(Pageable pageable);

  User readById(Long userId);

  void delete(Long id);

  void changePassword(ChangePasswordForm changeForm);

}
