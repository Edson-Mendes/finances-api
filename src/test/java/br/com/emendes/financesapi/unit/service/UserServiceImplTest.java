package br.com.emendes.financesapi.unit.service;

import br.com.emendes.financesapi.dto.request.ChangePasswordRequest;
import br.com.emendes.financesapi.dto.request.SignupRequest;
import br.com.emendes.financesapi.dto.response.UserResponse;
import br.com.emendes.financesapi.exception.*;
import br.com.emendes.financesapi.model.entity.User;
import br.com.emendes.financesapi.repository.UserRepository;
import br.com.emendes.financesapi.service.impl.UserServiceImpl;
import br.com.emendes.financesapi.util.component.CurrentAuthenticationComponent;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static br.com.emendes.financesapi.util.constant.ConstantForTesting.USER_PAGEABLE;
import static br.com.emendes.financesapi.util.faker.UserFaker.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * UserServiceImple unit tests.
 */
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
  private CurrentAuthenticationComponent currentAuthenticationComponentMock;

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
      when(userRepositoryMock.save(any(User.class))).thenReturn(user());

      SignupRequest signupRequest = SignupRequest.builder()
          .name("John Doe")
          .email("john.doe@email.com")
          .password("1234567890")
          .confirm("1234567890")
          .build();

      UserResponse actualUserResponse = userServiceImpl.createAccount(signupRequest);

      assertThat(actualUserResponse).isNotNull();
      assertThat(actualUserResponse.getEmail()).isEqualTo("john.doe@email.com");
      assertThat(actualUserResponse.getName()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("createAccount must throws DataConflictException when email already used")
    void createAccount_MustThrowsDataConflictException_WhenEmailAlreadyUsed() {
      when(userRepositoryMock.save(any()))
          .thenThrow(new DataIntegrityViolationException("Email inserido já está em uso!"));

      SignupRequest signupRequest = SignupRequest.builder()
          .name("John Doe")
          .email("email.alreay.in.use@email.com")
          .password("1234567890")
          .confirm("1234567890")
          .build();

      assertThatExceptionOfType(DataConflictException.class)
          .isThrownBy(() -> userServiceImpl.createAccount(signupRequest))
          .withMessage("Email is already in use");
    }

    @Test
    @DisplayName("createAccount must throws PasswordsDoNotMatchException when password no matches")
    void createAccount_MustThrowsPasswordsDoNotMatchException_WhenPasswordNoMatches() {
      SignupRequest signupRequest = SignupRequest.builder()
          .name("John Doe")
          .email("john.doe@email.com")
          .password("1234567890")
          .confirm("12345678")
          .build();

      assertThatExceptionOfType(PasswordsDoNotMatchException.class)
          .isThrownBy(() -> userServiceImpl.createAccount(signupRequest))
          .withMessage("Password and confirm does not match");
    }

  }

  @Nested
  @DisplayName("Tests for read method")
  class ReadMethod {

    @Test
    @DisplayName("read must return Page<UserResponse> when read successfully")
    void read_ReturnsPageUserResponse_WhenReadSuccessfully() {
      when(userRepositoryMock.findAll(USER_PAGEABLE))
          .thenReturn(new PageImpl<>(userList()));

      Page<UserResponse> actualUserResponsePage = userServiceImpl.read(USER_PAGEABLE);

      assertThat(actualUserResponsePage).isNotEmpty().hasSize(1);
      assertThat(actualUserResponsePage.getContent().get(0).getName())
          .isEqualTo("John Doe");
    }

  }

  @Nested
  @DisplayName("Tests for readById method")
  class ReadByIdMethod {

    @Test
    @DisplayName("readById must return User when read by id successfully")
    void readById_MustReturnsUser_WhenReadByIdSuccessfully() {
      when(userRepositoryMock.findById(1_000L))
          .thenReturn(userOptional());

      Long userId = 1_000L;
      User user = userServiceImpl.readById(userId);

      assertThat(user).isNotNull();
      assertThat(user.getId()).isEqualTo(1_000L);
    }

    @Test
    @DisplayName("readById must return Null when not found user")
    void readById_MustReturnsNull_WhenNotFoundUser() {
      Long NON_EXISTING_USER_ID = 9999L;
      when(userRepositoryMock.findById(NON_EXISTING_USER_ID))
          .thenReturn(Optional.empty());

      User user = userServiceImpl.readById(NON_EXISTING_USER_ID);

      assertThat(user).isNull();
    }

  }

  @Nested
  @DisplayName("Tests for delete method")
  class DeleteMethod {

    @Test
    @DisplayName("delete must call 1 time UserRepository#deleteById when delete successfully")
    void delete_MustCall1TimeUserRepositoryDeleteById_WhenDeleteSuccessfully() {
      when(userRepositoryMock.findById(1_000L)).thenReturn(userOptional());

      userServiceImpl.delete(1_000L);

      verify(userRepositoryMock).delete(any());
    }

    @Test
    @DisplayName("delete must throws EntityNotFoundException when not found user")
    void delete_MustThrowsEntityNotFoundException_WhenNotFoundUser() {
      when(userRepositoryMock.findById(1_000L)).thenReturn(Optional.empty());

      assertThatExceptionOfType(EntityNotFoundException.class)
          .isThrownBy(() -> userServiceImpl.delete(1_000L))
          .withMessageContaining("User not found with id " + 1_000L);
    }

  }

  @Nested
  @DisplayName("Tests for changePassword method")
  class ChangePasswordMethod {

    @Test
    @DisplayName("changePassword must save user with new password when change password successfully")
    void changePassword_MustSaveUserWithNewPassword_WhenChangePasswordSuccessfully() {
      User userToBeChanged = user();
      when(currentAuthenticationComponentMock.getCurrentUser()).thenReturn(userToBeChanged);
      when(passwordEncoderMock.matches(any(String.class), any(String.class))).thenReturn(true);
      when(passwordEncoderMock.encode(any())).thenReturn("encoded_password");
      when(userRepositoryMock.save(any(User.class))).thenReturn(user());

      ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.builder()
          .oldPassword("1234567890")
          .newPassword("123456789")
          .confirm("123456789")
          .build();

      userServiceImpl.changePassword(changePasswordRequest);

      verify(userRepositoryMock).save(any(User.class));
      assertThat(userToBeChanged).isNotNull();
      assertThat(userToBeChanged.getPassword()).isNotNull().isEqualTo("encoded_password");
    }

    @Test
    @DisplayName("changePassword must throws WrongPasswordException when password is wrong")
    void changePassword_MustThrowsWrongPasswordException_WhenPasswordIsWrong() {
      when(currentAuthenticationComponentMock.getCurrentUser()).thenReturn(user());
      when(passwordEncoderMock.matches(any(String.class), any(String.class))).thenReturn(false);

      ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.builder()
          .oldPassword("1234567890")
          .newPassword("123456789")
          .confirm("123456789")
          .build();

      assertThatExceptionOfType(WrongPasswordException.class)
          .isThrownBy(() -> userServiceImpl.changePassword(changePasswordRequest))
          .withMessageContaining("Wrong password");
    }

    @Test
    @DisplayName("changePassword must throws PasswordsDoNotMatchException when password and confirm no matches")
    void changePassword_MustThrowsPasswordsDoNotMatchException_WhenPasswordsAndConfirmNoMatches() {
      ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.builder()
          .oldPassword("1234567890")
          .newPassword("123456789")
          .confirm("1234567888")
          .build();

      assertThatExceptionOfType(PasswordsDoNotMatchException.class)
          .isThrownBy(() -> userServiceImpl.changePassword(changePasswordRequest))
          .withMessageContaining("Passwords do not match");
    }

    @Test
    @DisplayName("changePassword must throws UserIsNotAuthenticatedException when user is not authenticated")
    void changePassword_MustThrowsUserIsNotAuthenticatedException_WhenUserIsNotAuthenticated() {
      when(currentAuthenticationComponentMock.getCurrentUser())
          .thenThrow(new UserIsNotAuthenticatedException("User is not authenticated"));

      ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.builder()
          .oldPassword("1234567890")
          .newPassword("123456789")
          .confirm("123456789")
          .build();

      assertThatExceptionOfType(UserIsNotAuthenticatedException.class)
          .isThrownBy(() -> userServiceImpl.changePassword(changePasswordRequest))
          .withMessageContaining("User is not authenticated");
    }

  }

}
