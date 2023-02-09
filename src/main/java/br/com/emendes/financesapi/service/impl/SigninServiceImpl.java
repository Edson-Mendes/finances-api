package br.com.emendes.financesapi.service.impl;

import br.com.emendes.financesapi.config.security.service.TokenService;
import br.com.emendes.financesapi.controller.dto.TokenDto;
import br.com.emendes.financesapi.controller.form.LoginForm;
import br.com.emendes.financesapi.service.SigninService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SigninServiceImpl implements SigninService {

  private final AuthenticationManager authManager;
  private final TokenService tokenService;

  @Override
  public TokenDto login(LoginForm loginForm) {
    UsernamePasswordAuthenticationToken loginData = loginForm.converter();

    Authentication authentication = authManager.authenticate(loginData);
    String token = tokenService.generateToken(authentication);
    return new TokenDto(token, "Bearer");
  }

}
