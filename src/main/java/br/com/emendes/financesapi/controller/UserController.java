package br.com.emendes.financesapi.controller;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.emendes.financesapi.controller.dto.UserDto;
import br.com.emendes.financesapi.controller.form.ChangePasswordForm;
import br.com.emendes.financesapi.service.TokenService;
import br.com.emendes.financesapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/user")
public class UserController {

  @Autowired
  private UserService userService;

  @Autowired
  TokenService tokenService;

  @Operation(summary = "Buscar todos os usuários", security = { @SecurityRequirement(name = "bearer-key") })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Encontrou os usuários"),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "403", description = "Forbidden, usuário não tem permissão de acesso", content = @Content),
  })
  @GetMapping
  public ResponseEntity<Page<UserDto>> read(
      @PageableDefault(sort = "id", direction = Direction.ASC, page = 0, size = 10) Pageable pageable) {
    Page<UserDto> usersDto = userService.read(pageable);
    return ResponseEntity.ok(usersDto);
  }

  @Operation(summary = "Deletar usuário por id", security = { @SecurityRequirement(name = "bearer-key") })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Usuário deletado com sucesso"),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "403", description = "Forbidden, usuário não tem permissão de acesso", content = @Content),
      @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
  })
  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    userService.delete(id);
  }

  @Operation(summary = "Atualizar senha do usuário", security = { @SecurityRequirement(name = "bearer-key") })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Encontrou os usuários"),
      @ApiResponse(responseCode = "400", description = "Algum parâmetro do corpo da requisição inválido"),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
  })
  @PutMapping("/change-password")
  @Transactional
  public void changePassword(@Valid @RequestBody ChangePasswordForm changeForm, HttpServletRequest request) {
    Long userId = tokenService.getUserId(request);

    userService.changePassword(changeForm, userId);
  }

}
