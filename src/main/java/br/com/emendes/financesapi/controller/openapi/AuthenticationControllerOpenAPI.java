package br.com.emendes.financesapi.controller.openapi;

import br.com.emendes.financesapi.dto.response.TokenResponse;
import br.com.emendes.financesapi.dto.response.UserResponse;
import br.com.emendes.financesapi.dto.request.SignInRequest;
import br.com.emendes.financesapi.dto.request.SignupRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

@Tag(name = "Autenticação")
public interface AuthenticationControllerOpenAPI {

  @Operation(summary = "Logar um usuário")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200",
          description = "Logado com sucesso, retorna o token que deve ser enviado a cada requisição.",
          content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponse.class))}),
      @ApiResponse(responseCode = "400", description = "Bad request - Algum parâmetro do corpo da requisição inválido",
          content = @Content),
  })
  ResponseEntity<TokenResponse> signIn(SignInRequest form);

  @Operation(summary = "Cadastrar um usuário")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Cadastrado com sucesso", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))}),
      @ApiResponse(responseCode = "400", description = "Bad request - Algum parâmetro do corpo da requisição inválido.",
          content = @Content),
      @ApiResponse(responseCode = "409", description = "Email inserido já está em uso", content = @Content)
  })
  ResponseEntity<UserResponse> register(SignupRequest signupRequest, UriComponentsBuilder uriBuilder);

}
