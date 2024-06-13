package br.com.emendes.financesapi.integration.repository;

import br.com.emendes.financesapi.model.entity.User;
import br.com.emendes.financesapi.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.jdbc.SqlMergeMode;

import static br.com.emendes.financesapi.util.constant.SqlPath.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Integration tests da camada repository interagindo com o banco de dados.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("repository-it")
@DisplayName("Integration tests for UserRepository")
@SqlGroup({
    @Sql(scripts = {DROP_DATABASE_TABLES_SQL_PATH, CREATE_DATABASE_TABLES_SQL_PATH})
})
class UserRepositoryIT {

  @Autowired
  private UserRepository userRepository;

  @Nested
  @DisplayName("Save method")
  class SaveMethod {

    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    @SqlGroup({
        @Sql(scripts = {INSERT_USER_SQL_PATH}),
        @Sql(scripts = {DROP_DATABASE_TABLES_SQL_PATH}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    })
    @Test
    @DisplayName("save must save user entity when exists other user with same name")
    void save_MustSaveUserEntity_WhenExistsOtherUserWithSameName() {
      User user = User.builder()
          .name("John Doe")
          .email("john.other@email.com")
          .password("1234567890")
          .build();

      User actualSavedUser = userRepository.save(user);

      assertThat(actualSavedUser).isNotNull();
      assertThat(actualSavedUser.getId()).isNotNull();
    }

    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    @Sql(scripts = {INSERT_USER_SQL_PATH})
    @Test
    @DisplayName("save must throw DataIntegrityViolationException when exists other user with same email")
    void save_MustThrowDataIntegrityViolationException_WhenExistsOtherUserWithEmail() {
      User user = User.builder()
          .name("John D")
          .email("john.doe@email.com")
          .password("1234567890")
          .build();

      assertThatExceptionOfType(DataIntegrityViolationException.class)
          .isThrownBy(() -> userRepository.save(user));
    }

  }

}
