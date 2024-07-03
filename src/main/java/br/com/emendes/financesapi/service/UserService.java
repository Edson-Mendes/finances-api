package br.com.emendes.financesapi.service;

import br.com.emendes.financesapi.dto.request.ChangePasswordRequest;
import br.com.emendes.financesapi.dto.request.SignupRequest;
import br.com.emendes.financesapi.dto.response.UserResponse;
import br.com.emendes.financesapi.exception.EntityNotFoundException;
import br.com.emendes.financesapi.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Interface service com as abstrações para manipulação do recurso User.
 */
public interface UserService {

  /**
   * Registra uma conta (User) na base de dados.
   *
   * @param signupRequest objeto com as informações do usuário a ser registrado.
   * @return UserResponse dto com as informações do usuário registrado.
   */
  UserResponse createAccount(SignupRequest signupRequest);

  /**
   * Busca paginada de usuários.
   *
   * @param pageable objeto com os detalhes de como será feita a paginação.
   * @return {@code Page<UserResponse>} paginação com os usuários.
   */
  Page<UserResponse> read(Pageable pageable);

  /**
   * Busca usuário por id.
   *
   * @param userId identificador do usuário a ser buscado.
   * @return {@code User} para o dado id.
   */
  User readById(Long userId);

  /**
   * Deleta usuário por id.
   *
   * @param id identificador do usuário a ser deletado.
   * @throws EntityNotFoundException caso o usuário não seja encontrado.
   */
  void delete(Long id);

  /**
   * Atualiza a senha do usuário atual.
   *
   * @param changePasswordRequest objeto com contendo a nova senha do usuário.
   */
  void changePassword(ChangePasswordRequest changePasswordRequest);

}
