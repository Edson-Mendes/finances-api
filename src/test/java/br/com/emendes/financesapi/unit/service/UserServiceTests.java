package br.com.emendes.financesapi.unit.service;

import java.util.List;
import java.util.Optional;

import javax.persistence.NoResultException;

import br.com.emendes.financesapi.service.UserService;
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

import br.com.emendes.financesapi.controller.dto.UserDto;
import br.com.emendes.financesapi.controller.form.SignupForm;
import br.com.emendes.financesapi.util.creator.SignupFormCreator;
import br.com.emendes.financesapi.util.creator.UserCreator;
import br.com.emendes.financesapi.model.User;
import br.com.emendes.financesapi.repository.UserRepository;
import br.com.emendes.financesapi.validation.exception.DataConflictException;
import br.com.emendes.financesapi.validation.exception.PasswordsDoNotMatchException;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests for UserService")
public class UserServiceTests {

  @InjectMocks
  private UserService userService;

  @Mock
  private UserRepository userRepositoryMock;

  private final Pageable PAGEABLE = PageRequest.of(0, 10, Direction.ASC, "id");
  private final Long NON_EXISTING_USER_ID = 111111l;

  @BeforeEach
  public void setUp() {
    User userSaved = UserCreator.userSavedForUserServiceTests();
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

  }

  @Test
  @DisplayName("createAccount must return UserDto when create successfully")
  void createAccount_ReturnUserDto_WhenCreateSuccessfully() {
    SignupForm signupForm = SignupFormCreator.validSignupForm();

    UserDto createdUserDto = userService.createAccount(signupForm);

    Assertions.assertThat(createdUserDto).isNotNull();
    Assertions.assertThat(createdUserDto.getEmail()).isEqualTo(signupForm.getEmail());
    Assertions.assertThat(createdUserDto.getName()).isEqualTo(signupForm.getName());
  }

  @Test
  @DisplayName("createAccount must throws DataConflictException when email already used")
  void createAccount_ThrowsDataConflictException_WhenEmailAlreadyUsed() {
    BDDMockito.when(userRepositoryMock.save(ArgumentMatchers.any()))
        .thenThrow(new DataConflictException("Email inserido já está em uso!"));

    SignupForm signupForm = SignupFormCreator.validSignupForm();

    Assertions.assertThatExceptionOfType(DataConflictException.class)
        .isThrownBy(() -> userService.createAccount(signupForm));
  }

  @Test
  @DisplayName("createAccount must throws PasswordsDoNotMatchException when password don't matches")
  void createAccount_ThrowsPasswordsDoNotMatchException_WhenPassDontMatches() {

    SignupForm signupForm = SignupFormCreator.validSignupForm();
    signupForm.setConfirm("999999999999");

    Assertions.assertThatExceptionOfType(PasswordsDoNotMatchException.class)
        .isThrownBy(() -> userService.createAccount(signupForm));
  }

  @Test
  @DisplayName("read must return Page of UserDto when successful")
  void read_ReturnsPageOfUserDto_WhenSuccessful() {
    Page<UserDto> pageUserDto = userService.read(PAGEABLE);

    Assertions.assertThat(pageUserDto)
        .isNotEmpty()
        .hasSize(1);
    Assertions.assertThat(pageUserDto.getContent().get(0).getName())
        .isEqualTo(UserCreator.userSavedForUserServiceTests().getName());
  }

  @Test
  @DisplayName("readById must return User when successful")
  void readById_ReturnsUser_WhenSuccessful() {
    Long userId = 10000l;
    User user = userService.readById(userId);

    Assertions.assertThat(user)
        .isNotNull();
    Assertions.assertThat(user.getId())
        .isEqualTo(userId);
  }

  @Test
  @DisplayName("readById must return Null when not found user")
  void readById_ReturnsNull_WhenNotFoundUser() {
    Long userId = NON_EXISTING_USER_ID;
    User user = userService.readById(userId);

    Assertions.assertThat(user)
        .isNull();
  }

  @Test
  @DisplayName("delete must return Null when not found user")
  void delete_ThrowsNoResultException_WhenNotFoundUser() {
    Long userId = NON_EXISTING_USER_ID;

    Assertions.assertThatExceptionOfType(NoResultException.class)
        .isThrownBy(() -> userService.delete(userId))
        .withMessageContaining("não existe usuário com id: ");
  }

}
