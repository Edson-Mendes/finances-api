package br.com.emendes.financesapi.controller;

import java.net.URISyntaxException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.emendes.financesapi.config.security.TokenService;
import br.com.emendes.financesapi.controller.dto.TokenDto;
import br.com.emendes.financesapi.controller.dto.UserDto;
import br.com.emendes.financesapi.controller.form.LoginForm;
import br.com.emendes.financesapi.controller.form.SignupForm;
import br.com.emendes.financesapi.service.SignupService;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

  @Autowired
  private AuthenticationManager authManager;

  @Autowired
  private TokenService tokenService;

  @Autowired
  private SignupService signupService;

  @PostMapping("/signin")
  public ResponseEntity<TokenDto> auth(@RequestBody @Valid LoginForm form) {
    UsernamePasswordAuthenticationToken loginData = form.converter();

    Authentication authentication = authManager.authenticate(loginData);
    String token = tokenService.generateToken(authentication);

    return ResponseEntity.ok(new TokenDto(token, "Bearer"));
  }

  @PostMapping("/signup")
  public ResponseEntity<UserDto> register(@RequestBody @Valid SignupForm signupForm, UriComponentsBuilder uriBuilder)
      throws URISyntaxException {
    return signupService.createAccount(signupForm, uriBuilder);
  }

}
