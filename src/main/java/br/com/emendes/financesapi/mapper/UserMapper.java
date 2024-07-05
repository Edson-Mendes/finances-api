package br.com.emendes.financesapi.mapper;

import br.com.emendes.financesapi.dto.request.SignupRequest;
import br.com.emendes.financesapi.dto.response.UserResponse;
import br.com.emendes.financesapi.model.entity.User;

/**
 * Interface component com as abstrações para mapeamento do recurso User.
 */
public interface UserMapper {

  /**
   * Mepeia um objeto {@link SignupRequest} para User.
   *
   * @param signupRequest objeto a ser mapeado.
   * @return Objeto User.
   * @throws IllegalArgumentException caso signupRequest seja null.
   */
  User toUser(SignupRequest signupRequest);

  /**
   * Mapeia um objeto {@link User} para {@link UserResponse}.
   *
   * @param user objeto a ser mapeado.
   * @return Objeto UserResponse.
   * @throws IllegalArgumentException caso user seja null.
   */
  UserResponse toUserResponse(User user);

}
