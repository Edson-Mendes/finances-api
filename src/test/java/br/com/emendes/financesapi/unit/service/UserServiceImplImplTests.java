package br.com.emendes.financesapi.unit.service;

import java.util.List;
import java.util.Optional;

import javax.persistence.NoResultException;

import br.com.emendes.financesapi.controller.form.ChangePasswordForm;
import br.com.emendes.financesapi.service.impl.UserServiceImpl;
import br.com.emendes.financesapi.validation.exception.WrongPasswordException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.emendes.financesapi.dto.response.UserResponse;
import br.com.emendes.financesapi.dto.request.SignupRequest;
import br.com.emendes.financesapi.util.creator.SignupFormCreator;
import br.com.emendes.financesapi.util.creator.UserCreator;
import br.com.emendes.financesapi.model.entity.User;
import br.com.emendes.financesapi.repository.UserRepository;
import br.com.emendes.financesapi.validation.exception.DataConflictException;
import br.com.emendes.financesapi.validation.exception.PasswordsDoNotMatchException;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests for UserService")
class UserServiceImplImplTests {

  @InjectMocks
  private UserServiceImpl userServiceImpl;
  @Mock
  private UserRepository userRepositoryMock;

  private final Pageable PAGEABLE = PageRequest.of(0, 10, Direction.ASC, "id");
  private final Long NON_EXISTING_USER_ID = 111111L;

  @BeforeEach
  public void setUp() {
    User userSaved = UserCreator.userSavedForUserServiceTests();
    User userWithPasswordEncoded = UserCreator.userWithPasswordEncoded();
    Page<User> pageUser = new PageImpl<>(List.of(userSaved));

    BDDMockito.when(userRepositoryMock.findAll(PAGEABLE))
        .thenReturn(pageUser);

    BDDMockito.when(userRepositoryMock.save(ArgumentMatchers.any()))
        .thenReturn(userSaved);

    BDDMockito.when(userRepositoryMock.findById(userSaved.getId()))
        .thenReturn(Optional.of(userSaved));

    BDDMockito.when(userRepositoryMock.findById(NON_EXISTING_USER_ID))
        .thenReturn(Optional.empty());

    BDDMockito.doThrow(EmptyResultDataAccessException.class)
        .when(userRepositoryMock)
        .deleteById(NON_EXISTING_USER_ID);

    BDDMockito.when(userRepositoryMock.findCurrentUser())
        .thenReturn(Optional.of(userWithPasswordEncoded));

  }

  @Test
  @DisplayName("createAccount must return UserDto when create successfully")
  void createAccount_ReturnUserDto_WhenCreateSuccessfully() {
    SignupRequest signupRequest = SignupFormCreator.validSignupForm();

    UserResponse createdUserResponse = userServiceImpl.createAccount(signupRequest);

    Assertions.assertThat(createdUserResponse).isNotNull();
    Assertions.assertThat(createdUserResponse.getEmail()).isEqualTo(signupRequest.getEmail());
    Assertions.assertThat(createdUserResponse.getName()).isEqualTo(signupRequest.getName());
  }

  @Test
  @DisplayName("createAccount must throws DataConflictException when email already used")
  void createAccount_ThrowsDataConflictException_WhenEmailAlreadyUsed() {
    BDDMockito.when(userRepositoryMock.save(ArgumentMatchers.any()))
        .thenThrow(new DataConflictException("Email inserido já está em uso!"));

    SignupRequest signupRequest = SignupFormCreator.validSignupForm();

    Assertions.assertThatExceptionOfType(DataConflictException.class)
        .isThrownBy(() -> userServiceImpl.createAccount(signupRequest));
  }

  @Test
  @DisplayName("createAccount must throws PasswordsDoNotMatchException when password don't match")
  void createAccount_ThrowsPasswordsDoNotMatchException_WhenPassDontMatch() {

    SignupRequest signupRequest = SignupFormCreator.validSignupForm();
    signupRequest.setConfirm("999999999999");

    Assertions.assertThatExceptionOfType(PasswordsDoNotMatchException.class)
        .isThrownBy(() -> userServiceImpl.createAccount(signupRequest));
  }

  @Test
  @DisplayName("read must return Page of UserDto when successful")
  void read_ReturnsPageOfUserDto_WhenSuccessful() {
    Page<UserResponse> pageUserDto = userServiceImpl.read(PAGEABLE);

    Assertions.assertThat(pageUserDto)
        .isNotEmpty()
        .hasSize(1);
    Assertions.assertThat(pageUserDto.getContent().get(0).getName())
        .isEqualTo(UserCreator.userSavedForUserServiceTests().getName());
  }

  @Test
  @DisplayName("readById must return User when successful")
  void readById_ReturnsUser_WhenSuccessful() {
    Long userId = 10000L;
    User user = userServiceImpl.readById(userId);

    Assertions.assertThat(user)
        .isNotNull();
    Assertions.assertThat(user.getId())
        .isEqualTo(userId);
  }

  @Test
  @DisplayName("readById must return Null when not found user")
  void readById_ReturnsNull_WhenNotFoundUser() {
    User user = userServiceImpl.readById(NON_EXISTING_USER_ID);

    Assertions.assertThat(user)
        .isNull();
  }

  @Test
  @DisplayName("delete must throws NoResultException when not found user")
  void delete_ThrowsNoResultException_WhenNotFoundUser() {
    Long userId = NON_EXISTING_USER_ID;

    Assertions.assertThatExceptionOfType(NoResultException.class)
        .isThrownBy(() -> userServiceImpl.delete(userId))
        .withMessageContaining("não existe usuário com id: ");
  }

  @Test
  @DisplayName("changePassword must throws NoResultException when not found current user")
  void changePassword_ThrowsNoResultException_WhenNotFoundCurrentUser(){
    BDDMockito.when(userRepositoryMock.findCurrentUser()).thenReturn(Optional.empty());
    ChangePasswordForm changePasswordForm = new ChangePasswordForm(
        "123456", "1234567890", "1234567890");

    Assertions.assertThatExceptionOfType(NoResultException.class)
        .isThrownBy(() -> userServiceImpl.changePassword(changePasswordForm))
        .withMessageContaining("Não foi possível encontrar o usuário atual");
  }

  @Test
  @DisplayName("changePassword must throws PasswordsDoNotMatchException when password and confirm don't match")
  void changePassword_ThrowsPasswordsDoNotMatchException_WhenPasswordsDontMatch(){
    String newPassword = "1234567890";
    String confirmWhichDontMatch = "123456789";
    ChangePasswordForm changePasswordForm = new ChangePasswordForm(
        "123456", newPassword, confirmWhichDontMatch);

    Assertions.assertThatExceptionOfType(PasswordsDoNotMatchException.class)
        .isThrownBy(() -> userServiceImpl.changePassword(changePasswordForm))
        .withMessageContaining("as senhas não correspondem!");
  }

  @Test
  @DisplayName("changePassword must throws WrongPasswordException when password is wrong")
  void changePassword_ThrowsWrongPasswordException_WhenPasswordIsWrong(){
    String oldPassword = "1234";
    ChangePasswordForm changePasswordForm = new ChangePasswordForm(
        oldPassword, "1234567890", "1234567890");

    Assertions.assertThatExceptionOfType(WrongPasswordException.class)
        .isThrownBy(() -> userServiceImpl.changePassword(changePasswordForm))
        .withMessageContaining("Senha incorreta");
  }
}
