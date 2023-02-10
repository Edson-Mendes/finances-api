package br.com.emendes.financesapi.service.impl;

import br.com.emendes.financesapi.config.security.service.TokenService;
import br.com.emendes.financesapi.dto.response.UserResponse;
import br.com.emendes.financesapi.dto.request.SignupRequest;
import br.com.emendes.financesapi.dto.request.SignInRequest;
import br.com.emendes.financesapi.dto.response.TokenResponse;
import br.com.emendes.financesapi.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

  private final AuthenticationManager authManager;
  private final TokenService tokenService;

  @Override
  public TokenResponse signIn(SignInRequest request) {
    UsernamePasswordAuthenticationToken loginData =
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());;

    Authentication authentication = authManager.authenticate(loginData);
    String token = tokenService.generateToken(authentication);
    return new TokenResponse(token, "Bearer");
  }

  // TODO: Implementar
  @Override
  public UserResponse register(SignupRequest signupRequest) {
    return null;
  }

}
