package br.com.emendes.financesapi.controller;

import br.com.emendes.financesapi.controller.openapi.AuthenticationControllerOpenAPI;
import br.com.emendes.financesapi.dto.request.SignInRequest;
import br.com.emendes.financesapi.dto.request.SignupRequest;
import br.com.emendes.financesapi.dto.response.TokenResponse;
import br.com.emendes.financesapi.dto.response.UserResponse;
import br.com.emendes.financesapi.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * Classe controller para lidar com os endpoints /api/auth/**.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/auth", produces = "application/json;charset=UTF-8")
public class AuthenticationController implements AuthenticationControllerOpenAPI {

  private final AuthenticationService authService;

  /**
   * Método responsável por POST /api/auth/signin.
   *
   * @param signInRequest objeto contendo as credenciais do usuário.
   */
  @Override
  @PostMapping("/signin")
  public ResponseEntity<TokenResponse> signIn(@RequestBody @Valid SignInRequest signInRequest) {
    TokenResponse tokenResponse = authService.signIn(signInRequest);

    return ResponseEntity.ok(tokenResponse);
  }

  /**
   * Método responsável por POST /api/auth/signup.
   *
   * @param signupRequest objeto contendo os dados de registro do usuário.
   */
  @Override
  @PostMapping("/signup")
  public ResponseEntity<UserResponse> register(
      @RequestBody @Valid SignupRequest signupRequest, UriComponentsBuilder uriBuilder) {
    UserResponse userResponse = authService.register(signupRequest);
    URI uri = uriBuilder.path("/user/{id}").buildAndExpand(userResponse.getId()).toUri();
    return ResponseEntity.created(uri).body(userResponse);
  }

}
