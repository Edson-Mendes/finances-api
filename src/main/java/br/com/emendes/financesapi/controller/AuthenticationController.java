package br.com.emendes.financesapi.controller;

import java.net.URISyntaxException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.emendes.financesapi.config.security.TokenService;
import br.com.emendes.financesapi.config.validation.error_dto.ErrorDto;
import br.com.emendes.financesapi.controller.dto.TokenDto;
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
  public ResponseEntity<?> auth(@RequestBody @Valid LoginForm form){
    UsernamePasswordAuthenticationToken loginData = form.converter();

    try {
      Authentication authentication = authManager.authenticate(loginData);
      String token = tokenService.generateToken(authentication);

      return ResponseEntity.ok(new TokenDto(token, "Bearer"));
    } catch (AuthenticationException e) {
      
      ErrorDto errorDto = new ErrorDto("Bad credentials", "Email ou password inválidos");
      return ResponseEntity.badRequest().header("Content-Type", "application/json;charset=UTF-8").body(errorDto);
    }
  }

  @PostMapping("/signup")
  public ResponseEntity<?> register(@RequestBody @Valid SignupForm signupForm, UriComponentsBuilder uriBuilder) throws URISyntaxException{
    return signupService.createAccount(signupForm, uriBuilder);
  }

}
