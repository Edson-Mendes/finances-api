package br.com.emendes.financesapi.controller.openapi;

import br.com.emendes.financesapi.dto.response.UserResponse;
import br.com.emendes.financesapi.controller.form.ChangePasswordRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

@SecurityRequirement(name = "bearer-key")
@Tag(name = "Usuários")
public interface UserControllerOpenAPI {

  @Operation(summary = "Buscar todos os usuários", security = {})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Encontrou os usuários"),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "403", description = "Forbidden, usuário não tem permissão de acesso",
          content = @Content),
  })
  ResponseEntity<Page<UserResponse>> readAll(@ParameterObject Pageable pageable);

  @Operation(summary = "Deletar usuário por id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso"),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "403", description = "Forbidden, usuário não tem permissão de acesso",
          content = @Content),
      @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
  })
  ResponseEntity<Void> delete(Long id);

  @Operation(summary = "Atualizar senha do usuário")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Senha atualizada com sucesso"),
      @ApiResponse(responseCode = "400", description = "Algum parâmetro do corpo da requisição inválido"),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
  })
  ResponseEntity<Void> changePassword(ChangePasswordRequest changeForm);

}
