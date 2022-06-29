package br.com.emendes.financesapi.unit.repository;

import java.util.Optional;

import br.com.emendes.financesapi.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import br.com.emendes.financesapi.util.creator.UserCreator;
import br.com.emendes.financesapi.model.User;

@DataJpaTest
@DisplayName("Tests for UserRepository")
public class UserRepositoryTests {

  @Autowired
  private UserRepository userRepository;

  @Test
  @DisplayName("findByEmail must returns user when successful")
  void findByEmail_ReturnsUser_WhenSuccessful() {
    User userToBeSaved = UserCreator.validUserForUserRepositoryTest();
    User userSaved = this.userRepository.save(userToBeSaved);

    String email = userSaved.getEmail();

    Optional<User> userOptional = this.userRepository.findByEmail(email);

    Assertions.assertThat(userOptional).isNotEmpty();
    Assertions.assertThat(userOptional.get().getId()).isEqualTo(userSaved.getId());
    Assertions.assertThat(userOptional.get().getName()).isEqualTo(userSaved.getName());
    Assertions.assertThat(userOptional.get().getEmail()).isEqualTo(email);
  }

  @Test
  @DisplayName("findByEmail must return empty optional when user not found")
  void findByEmail_ReturnsEmptyOptional_WhenUserNotFound() {
    String email = "userdontexist@email.com";

    Optional<User> userOptional = this.userRepository.findByEmail(email);

    Assertions.assertThat(userOptional).isEmpty();
  }

  @Test
  @DisplayName("findByIdAndPassword must return user when successful")
  void findByIdAndPassword_ReturnsUser_WhenSuccessful() {
    User userToBeSaved = UserCreator.validUserForUserRepositoryTest();
    User userSaved = this.userRepository.save(userToBeSaved);

    String password = userSaved.getPassword();
    Long id = userSaved.getId();

    Optional<User> userOptional = this.userRepository.findByIdAndPassword(id, password);

    Assertions.assertThat(userOptional).isNotEmpty();
    Assertions.assertThat(userOptional.get().getEmail()).isEqualTo(userSaved.getEmail());

  }

  @Test
  @DisplayName("findByIdAndPassword must return empty optional when userId not found")
  void findByIdAndPassword_ReturnsEmptyOptionalUser_WhenUserIdNotFound() {
    User userToBeSaved = UserCreator.validUserForUserRepositoryTest();
    User userSaved = this.userRepository.save(userToBeSaved);

    String password = userSaved.getPassword();
    Long id = 999l;

    Optional<User> userOptional = this.userRepository.findByIdAndPassword(id, password);

    Assertions.assertThat(userOptional).isEmpty();
  }

  @Test
  @DisplayName("findByIdAndPassword must return empty optional when password don't match")
  void findByIdAndPassword_ReturnsEmptyOptionalUser_WhenPasswordDontMatch() {
    User userToBeSaved = UserCreator.validUserForUserRepositoryTest();
    User userSaved = this.userRepository.save(userToBeSaved);

    String password = "1234";
    Long id = userSaved.getId();

    Optional<User> userOptional = this.userRepository.findByIdAndPassword(id, password);

    Assertions.assertThat(userOptional).isEmpty();
  }

}
