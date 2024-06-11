package br.com.emendes.financesapi.unit.service;

import br.com.emendes.financesapi.dto.request.ChangePasswordRequest;
import br.com.emendes.financesapi.dto.request.SignupRequest;
import br.com.emendes.financesapi.dto.response.UserResponse;
import br.com.emendes.financesapi.exception.DataConflictException;
import br.com.emendes.financesapi.exception.EntityNotFoundException;
import br.com.emendes.financesapi.exception.PasswordsDoNotMatchException;
import br.com.emendes.financesapi.exception.WrongPasswordException;
import br.com.emendes.financesapi.model.entity.User;
import br.com.emendes.financesapi.repository.UserRepository;
import br.com.emendes.financesapi.service.impl.UserServiceImpl;
import br.com.emendes.financesapi.util.AuthenticationFacade;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static br.com.emendes.financesapi.util.constant.ConstantForTesting.ROLE_USER;
import static br.com.emendes.financesapi.util.constant.ConstantForTesting.USER;
import static br.com.emendes.financesapi.util.faker.UserFaker.optionalUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests for UserServiceImpl")
class UserServiceImplTest {

  @InjectMocks
  private UserServiceImpl userServiceImpl;
  @Mock
  private UserRepository userRepositoryMock;
  @Mock
  private PasswordEncoder passwordEncoderMock;
  @Mock
  private AuthenticationFacade authenticationFacadeMock;

  private final Pageable PAGEABLE = PageRequest.of(0, 10, Direction.ASC, "id");
  private final Long NON_EXISTING_USER_ID = 9999L;

  @Nested
  @DisplayName("Tests for createAccount method")
  class CreateAccountMethod {

    @BeforeEach
    void setUp() {
      when(passwordEncoderMock.encode(any(String.class)))
          .thenReturn("encodedpassword");
    }

    @Test
    @DisplayName("createAccount must return UserResponse when create successfully")
    void createAccount_MustReturnUserResponse_WhenCreateSuccessfully() {
      when(userRepositoryMock.save(any(User.class)))
          .thenReturn(USER);

      SignupRequest signupRequest = SignupRequest.builder()
          .name("Lorem Ipsum")
          .email("lorem@email.com")
          .password("1234567890")
          .confirm("1234567890")
          .build();

      UserResponse actualUserResponse = userServiceImpl.createAccount(signupRequest);

      Assertions.assertThat(actualUserResponse).isNotNull();
      Assertions.assertThat(actualUserResponse.getEmail()).isEqualTo("lorem@email.com");
      Assertions.assertThat(actualUserResponse.getName()).isEqualTo("Lorem Ipsum");
    }

    @Test
    @DisplayName("createAccount must throws DataConflictException when email already used")
    void createAccount_MustThrowsDataConflictException_WhenEmailAlreadyUsed() {
      when(userRepositoryMock.save(any()))
          .thenThrow(new DataIntegrityViolationException("Email inserido já está em uso!"));

      SignupRequest signupRequest = SignupRequest.builder()
          .name("Lorem Ipsum")
          .email("email.alreay.in.use@email.com")
          .password("1234567890")
          .confirm("1234567890")
          .build();

      Assertions.assertThatExceptionOfType(DataConflictException.class)
          .isThrownBy(() -> userServiceImpl.createAccount(signupRequest))
          .withMessage("Email is already in use");
    }

    @Test
    @DisplayName("createAccount must throws PasswordsDoNotMatchException when password no matches")
    void createAccount_MustThrowsPasswordsDoNotMatchException_WhenPasswordNoMatches() {
      SignupRequest signupRequest = SignupRequest.builder()
          .name("Lorem Ipsum")
          .email("lorem@email.com")
          .password("1234567890")
          .confirm("12345678")
          .build();

      Assertions.assertThatExceptionOfType(PasswordsDoNotMatchException.class)
          .isThrownBy(() -> userServiceImpl.createAccount(signupRequest))
          .withMessage("Password and confirm do not match");
    }

  }

  @Nested
  @DisplayName("Tests for read method")
  class ReadMethod {

    @Test
    @DisplayName("read must return Page<UserResponse> when read successfully")
    void read_ReturnsPageUserResponse_WhenReadSuccessfully() {
      when(userRepositoryMock.findAll(PAGEABLE))
          .thenReturn(new PageImpl<>(List.of(USER)));

      Page<UserResponse> actualUserResponsePage = userServiceImpl.read(PAGEABLE);

      Assertions.assertThat(actualUserResponsePage).isNotEmpty().hasSize(1);
      Assertions.assertThat(actualUserResponsePage.getContent().get(0).getName())
          .isEqualTo("Lorem Ipsum");
    }

  }

  @Nested
  @DisplayName("Tests for readById method")
  class ReadByIdMethod {

    @Test
    @DisplayName("readById must return User when read by id successfully")
    void readById_MustReturnsUser_WhenReadByIdSuccessfully() {
      when(userRepositoryMock.findById(1000L))
          .thenReturn(Optional.of(USER));

      Long userId = 1000L;
      User user = userServiceImpl.readById(userId);

      Assertions.assertThat(user).isNotNull();
      Assertions.assertThat(user.getId()).isEqualTo(1000L);
    }

    @Test
    @DisplayName("readById must return Null when not found user")
    void readById_MustReturnsNull_WhenNotFoundUser() {
      when(userRepositoryMock.findById(NON_EXISTING_USER_ID))
          .thenReturn(Optional.empty());

      User user = userServiceImpl.readById(NON_EXISTING_USER_ID);

      Assertions.assertThat(user).isNull();
    }

  }

  @Nested
  @DisplayName("Tests for delete method")
  class DeleteMethod {

    @Test
    @DisplayName("delete must call 1 time UserRepository#deleteById when delete successfully")
    void delete_MustCall1TimeUserRepositoryDeleteById_WhenDeleteSuccessfully() {
      when(userRepositoryMock.findById(1_000L)).thenReturn(optionalUser());

      userServiceImpl.delete(1_000L);

      verify(userRepositoryMock).delete(any());
    }

    @Test
    @DisplayName("delete must throws EntityNotFoundException when not found user")
    void delete_MustThrowsEntityNotFoundException_WhenNotFoundUser() {
      when(userRepositoryMock.findById(1_000L)).thenReturn(Optional.empty());

      Assertions.assertThatExceptionOfType(EntityNotFoundException.class)
          .isThrownBy(() -> userServiceImpl.delete(1_000L))
          .withMessageContaining("User not found with id " + 1_000L);
    }

  }

  @Nested
  @DisplayName("Tests for changePassword method")
  class ChangePasswordMethod {

    @BeforeEach
    void setUp() {
      when(authenticationFacadeMock.getAuthentication())
          .thenReturn(new TestingAuthenticationToken(USER, null, List.of(ROLE_USER)));
    }

    @Test
    @DisplayName("changePassword must save user with new password when change password successfully")
    void changePassword_MustSaveUserWithNewPassword_WhenChangePasswordSuccessfully() {
      when(passwordEncoderMock.matches(any(String.class), any(String.class)))
          .thenReturn(true);
      when(userRepositoryMock.save(any(User.class)))
          .thenReturn(USER);

      ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.builder()
          .oldPassword("123456789")
          .newPassword("1234567890")
          .confirm("1234567890")
          .build();

      userServiceImpl.changePassword(changePasswordRequest);

      verify(userRepositoryMock).save(any(User.class));
    }

    @Test
    @DisplayName("changePassword must throws WrongPasswordException when password is wrong")
    void changePassword_MustThrowsWrongPasswordException_WhenPasswordIsWrong() {
      when(passwordEncoderMock.matches(any(String.class), any(String.class)))
          .thenReturn(false);

      ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.builder()
          .oldPassword("123456789")
          .newPassword("1234567890")
          .confirm("1234567890")
          .build();

      Assertions.assertThatExceptionOfType(WrongPasswordException.class)
          .isThrownBy(() -> userServiceImpl.changePassword(changePasswordRequest))
          .withMessageContaining("Wrong password");
    }

    @Test
    @DisplayName("changePassword must throws PasswordsDoNotMatchException when password and confirm no matches")
    void changePassword_MustThrowsPasswordsDoNotMatchException_WhenPasswordsAndConfirmNoMatches() {
      when(passwordEncoderMock.matches(any(String.class), any(String.class)))
          .thenReturn(true);

      ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.builder()
          .oldPassword("123456789")
          .newPassword("1234567890")
          .confirm("12345678")
          .build();

      Assertions.assertThatExceptionOfType(PasswordsDoNotMatchException.class)
          .isThrownBy(() -> userServiceImpl.changePassword(changePasswordRequest))
          .withMessageContaining("Passwords do not match");
    }

  }

}
