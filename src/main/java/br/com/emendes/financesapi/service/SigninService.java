package br.com.emendes.financesapi.service;

import br.com.emendes.financesapi.controller.dto.TokenDto;
import br.com.emendes.financesapi.controller.form.LoginForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class SigninService {

  @Autowired
  private AuthenticationManager authManager;
  @Autowired
  private TokenService tokenService;

  public TokenDto login(LoginForm loginForm){
    UsernamePasswordAuthenticationToken loginData = loginForm.converter();

    Authentication authentication = authManager.authenticate(loginData);
    String token = tokenService.generateToken(authentication);
    return new TokenDto(token, "Bearer");
  }

}
