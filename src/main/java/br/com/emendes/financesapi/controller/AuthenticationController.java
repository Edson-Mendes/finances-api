package br.com.emendes.financesapi.controller;

import java.net.URI;
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

import br.com.emendes.financesapi.controller.dto.TokenDto;
import br.com.emendes.financesapi.controller.dto.UserDto;
import br.com.emendes.financesapi.controller.form.LoginForm;
import br.com.emendes.financesapi.controller.form.SignupForm;
import br.com.emendes.financesapi.service.TokenService;
import br.com.emendes.financesapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

  @Autowired
  private AuthenticationManager authManager;

  @Autowired
  private TokenService tokenService;

  @Autowired
  private UserService userService;

  @Operation(summary = "Logar um usuário", tags = { "Autenticação" })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Logado com sucesso, retorna o token que deve ser enviado a cada requisição", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = TokenDto.class)) }),
      @ApiResponse(responseCode = "400", description = "Bad request - Algum parâmetro do corpo da requisição inválido", content = @Content),
  })
  @PostMapping("/signin")
  public ResponseEntity<TokenDto> auth(@RequestBody @Valid LoginForm form) {
    UsernamePasswordAuthenticationToken loginData = form.converter();

    Authentication authentication = authManager.authenticate(loginData);
    String token = tokenService.generateToken(authentication);

    return ResponseEntity.ok(new TokenDto(token, "Bearer"));
  }

  @Operation(summary = "Cadastrar um usuário", tags = { "Autenticação" })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Cadastrado com sucesso", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class)) }),
      @ApiResponse(responseCode = "400", description = "Bad request - Algum parâmetro do corpo da requisição inválido", content = @Content),
      @ApiResponse(responseCode = "409", description = "Email inserido já está em uso", content = @Content)
  })
  @PostMapping("/signup")
  public ResponseEntity<UserDto> register(@RequestBody @Valid SignupForm signupForm, UriComponentsBuilder uriBuilder)
      throws URISyntaxException {
    UserDto userDto = userService.createAccount(signupForm);
    URI uri = uriBuilder.path("/user/{id}").buildAndExpand(userDto.getId()).toUri();
    return ResponseEntity.created(uri).body(userDto);
  }

}
