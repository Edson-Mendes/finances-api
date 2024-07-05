package br.com.emendes.financesapi.unit.mapper;

import br.com.emendes.financesapi.dto.request.SignupRequest;
import br.com.emendes.financesapi.dto.response.UserResponse;
import br.com.emendes.financesapi.mapper.impl.UserMapperImpl;
import br.com.emendes.financesapi.model.entity.User;
import br.com.emendes.financesapi.util.faker.RoleFaker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Unit tests for UserMapperImpl.
 */
@ExtendWith(SpringExtension.class)
@DisplayName("Unit tests for UserMapperImpl")
class UserMapperImplTest {

  @InjectMocks
  private UserMapperImpl userMapper;

  @Nested
  @DisplayName("Tests for toUser method")
  class ToUserMethod {

    @Test
    @DisplayName("toUser must return User when map successfully")
    void toUser_MustReturnUser_WhenMapSuccessfully() {
      SignupRequest signupRequest = SignupRequest.builder()
          .name("John Doe")
          .email("john.doe@email.com")
          .password("1234567890")
          .confirm("1234567890")
          .build();

      User actualUser = userMapper.toUser(signupRequest);

      assertThat(actualUser).isNotNull();
      assertThat(actualUser.getId()).isNull();
      assertThat(actualUser.getName()).isNotNull().isEqualTo("John Doe");
      assertThat(actualUser.getEmail()).isNotNull().isEqualTo("john.doe@email.com");
      assertThat(actualUser.getPassword()).isNotNull().isEqualTo("1234567890");
    }

    @Test
    @DisplayName("toUser must throw IllegalArgumentException when signupRequest is null")
    void toUser_MustThrowIllegalArgumentException_WhenSignupRequestIsNull() {
      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> userMapper.toUser(null))
          .withMessage("signupRequest must not be null.");
    }

  }

  @Nested
  @DisplayName("Tests for toUserResponse method")
  class ToUserResponseMethod {

    @Test
    @DisplayName("toUserResponse must return UserResponse when map successfully")
    void toUserResponse_MustReturnUserResponse_WhenMapSuccessfully() {
      User user = User.builder()
          .id(1_000L)
          .name("John Doe")
          .email("john.doe@email.com")
          .password("1234567890")
          .roles(List.of(RoleFaker.userRole()))
          .build();

      UserResponse actualUserResponse = userMapper.toUserResponse(user);

      assertThat(actualUserResponse).isNotNull();
      assertThat(actualUserResponse.getId()).isNotNull().isEqualTo(1_000L);
      assertThat(actualUserResponse.getName()).isNotNull().isEqualTo("John Doe");
      assertThat(actualUserResponse.getEmail()).isNotNull().isEqualTo("john.doe@email.com");
    }

    @Test
    @DisplayName("toUserResponse must throw IllegalArgumentException when user is null")
    void toUserResponse_MustThrowIllegalArgumentException_WhenUserIsNull() {
      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> userMapper.toUserResponse(null))
          .withMessage("user must not be null.");
    }

  }

}
