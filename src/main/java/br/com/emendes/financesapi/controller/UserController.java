package br.com.emendes.financesapi.controller;

import br.com.emendes.financesapi.controller.dto.UserDto;
import br.com.emendes.financesapi.controller.form.ChangePasswordForm;
import br.com.emendes.financesapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

  @Autowired
  private UserService userService;

  @Operation(summary = "Buscar todos os usuários", tags = { "Usuários" }, security = {
      @SecurityRequirement(name = "bearer-key") })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Encontrou os usuários"),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "403", description = "Forbidden, usuário não tem permissão de acesso", content = @Content),
  })
  @GetMapping
  public ResponseEntity<Page<UserDto>> readAll(
      @ParameterObject @PageableDefault(sort = "id", direction = Direction.ASC) Pageable pageable) {
    Page<UserDto> usersDto = userService.read(pageable);
    return ResponseEntity.ok(usersDto);
  }

  @Operation(summary = "Deletar usuário por id", tags = { "Usuários" }, security = {
      @SecurityRequirement(name = "bearer-key") })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso"),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "403", description = "Forbidden, usuário não tem permissão de acesso", content = @Content),
      @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    userService.delete(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Operation(summary = "Atualizar senha do usuário", tags = { "Usuários" }, security = {
      @SecurityRequirement(name = "bearer-key") })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Senha atualizada com sucesso"),
      @ApiResponse(responseCode = "400", description = "Algum parâmetro do corpo da requisição inválido"),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
  })
  @PutMapping("/password")
  @Transactional
  public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordForm changeForm) {
    userService.changePassword(changeForm);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
