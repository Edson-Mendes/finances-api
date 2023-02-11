package br.com.emendes.financesapi.service.impl;

import br.com.emendes.financesapi.config.security.service.TokenService;
import br.com.emendes.financesapi.dto.response.TokenResponse;
import br.com.emendes.financesapi.dto.request.SignInRequest;
import br.com.emendes.financesapi.service.SigninService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

// TODO: Excluir!
@RequiredArgsConstructor
@Service
@Deprecated
public class SigninServiceImpl implements SigninService {

  private final AuthenticationManager authManager;
  private final TokenService tokenService;

  @Override
  public TokenResponse login(SignInRequest signInRequest) {
    UsernamePasswordAuthenticationToken loginData = signInRequest.converter();

    Authentication authentication = authManager.authenticate(loginData);
    String token = tokenService.generateToken(authentication);
    return new TokenResponse(token, "Bearer");
  }

}
