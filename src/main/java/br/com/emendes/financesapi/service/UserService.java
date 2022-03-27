package br.com.emendes.financesapi.service;

import javax.persistence.NoResultException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import br.com.emendes.financesapi.config.validation.exception.PasswordsDoNotMatchException;
import br.com.emendes.financesapi.controller.form.ChangePasswordForm;
import br.com.emendes.financesapi.model.User;
import br.com.emendes.financesapi.repository.UserRepository;

@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  public void delete(Long id) {
    try {
      userRepository.deleteById(id);
    } catch (EmptyResultDataAccessException e) {
      throw new NoResultException("não existe usuário com id: " + id);
    }
  }

  public void changePassword(@Valid ChangePasswordForm changeForm, Long userId) {
    if (changeForm.isMatch()) {
      User user = userRepository.findById(userId).get();
      user.setPassword(changeForm.getNewPasswordEncoded());
    } else {
      throw new PasswordsDoNotMatchException("as senhas não correspondem!");
    }
  }

}
