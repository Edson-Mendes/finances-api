package br.com.emendes.financesapi.service;

import br.com.emendes.financesapi.controller.dto.TokenDto;
import br.com.emendes.financesapi.controller.form.LoginForm;

public interface SigninService {

  TokenDto login(LoginForm loginForm);

}
