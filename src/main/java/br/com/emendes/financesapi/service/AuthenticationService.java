package br.com.emendes.financesapi.service;

import br.com.emendes.financesapi.dto.response.TokenResponse;
import br.com.emendes.financesapi.dto.response.UserResponse;
import br.com.emendes.financesapi.dto.request.SignInRequest;
import br.com.emendes.financesapi.dto.request.SignupRequest;

public interface AuthenticationService {

  TokenResponse signIn(SignInRequest request);

  UserResponse register(SignupRequest request);

}
