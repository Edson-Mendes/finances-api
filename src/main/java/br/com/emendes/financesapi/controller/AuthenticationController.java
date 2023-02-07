package br.com.emendes.financesapi.controller;

import br.com.emendes.financesapi.controller.dto.TokenDto;
import br.com.emendes.financesapi.controller.dto.UserDto;
import br.com.emendes.financesapi.controller.form.LoginForm;
import br.com.emendes.financesapi.controller.form.SignupForm;
import br.com.emendes.financesapi.controller.openapi.AuthenticationControllerOpenAPI;
import br.com.emendes.financesapi.service.SigninService;
import br.com.emendes.financesapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController implements AuthenticationControllerOpenAPI {

  @Autowired
  private UserService userService;
  @Autowired
  private SigninService signinService;


  @Override
  @PostMapping("/signin")
  public ResponseEntity<TokenDto> auth(@RequestBody @Valid LoginForm form) {
    TokenDto tokenDto = signinService.login(form);

    return ResponseEntity.ok(tokenDto);

  }

  @Override
  @PostMapping("/signup")
  public ResponseEntity<UserDto> register(@RequestBody @Valid SignupForm signupForm, UriComponentsBuilder uriBuilder) {
    UserDto userDto = userService.createAccount(signupForm);
    URI uri = uriBuilder.path("/user/{id}").buildAndExpand(userDto.getId()).toUri();
    return ResponseEntity.created(uri).body(userDto);
  }

}
