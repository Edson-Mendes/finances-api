package br.com.emendes.financesapi.util.faker;

import br.com.emendes.financesapi.dto.response.UserResponse;
import br.com.emendes.financesapi.model.entity.User;

import java.util.List;
import java.util.Optional;

import static br.com.emendes.financesapi.util.faker.RoleFaker.userRole;

/**
 * Classe com objetos relacionados a User para serem usados em testes automatizados.
 */
public class UserFaker {

  public static final long USER_ID = 1_000L;
  public static final String USER_NAME = "John Doe";
  public static final String USER_EMAIL = "john.doe@email.com";
  public static final String USER_PASSWORD = "1234567890";

  /**
   * Retorna um User com todos os campos.
   */
  public static User user() {
    return User.builder()
        .id(USER_ID)
        .name(USER_NAME)
        .email(USER_EMAIL)
        .password(USER_PASSWORD)
        .roles(List.of(userRole()))
        .build();
  }

  /**
   * Retorna um User sem o campo id.
   */
  public static User nonSavedUser() {
    return User.builder()
        .name(USER_NAME)
        .email(USER_EMAIL)
        .password(USER_PASSWORD)
        .build();
  }

  /**
   * Retorna um {@code Optional<User>} que contém um User.
   */
  public static Optional<User> userOptional() {
    return Optional.ofNullable(user());
  }

  /**
   * Retorna um {@code List<User>} com um elemento.
   */
  public static List<User> userList() {
    return List.of(user());
  }

  /**
   * Retorna um {@code UserResponse} com todos os campos.
   */
  public static UserResponse userResponse() {
    return UserResponse.builder()
        .id(USER_ID)
        .name(USER_NAME)
        .email(USER_EMAIL)
        .build();
  }

}
