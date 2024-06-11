package br.com.emendes.financesapi.util.faker;

import br.com.emendes.financesapi.model.entity.User;

import java.util.List;
import java.util.Optional;

import static br.com.emendes.financesapi.util.faker.RoleFaker.userRole;

/**
 * Classe com objetos relacionados a User para serem usados em testes automatizados.
 */
public class UserFaker {

  /**
   * Retorna um User com todos os dados.
   */
  public static User user() {
    return User.builder()
        .id(1_000L)
        .name("John Doe")
        .email("john.doe@email.com")
        .password("1234567890")
        .roles(List.of(userRole()))
        .build();
  }

  /**
   * Retorna um {@code Optional<User>} que contém um User.
   */
  public static Optional<User> optionalUser() {
    return Optional.ofNullable(user());
  }

}
