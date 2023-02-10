package br.com.emendes.financesapi.service;

import br.com.emendes.financesapi.dto.response.TokenResponse;
import br.com.emendes.financesapi.dto.request.SignInRequest;

public interface SigninService {

  TokenResponse login(SignInRequest signInRequest);

}
