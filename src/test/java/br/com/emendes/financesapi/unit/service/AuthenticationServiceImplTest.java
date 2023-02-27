package br.com.emendes.financesapi.unit.service;

import br.com.emendes.financesapi.config.security.service.TokenService;
import br.com.emendes.financesapi.dto.request.SignInRequest;
import br.com.emendes.financesapi.dto.request.SignupRequest;
import br.com.emendes.financesapi.dto.response.TokenResponse;
import br.com.emendes.financesapi.dto.response.UserResponse;
import br.com.emendes.financesapi.service.UserService;
import br.com.emendes.financesapi.service.impl.AuthenticationServiceImpl;
import br.com.emendes.financesapi.exception.DataConflictException;
import br.com.emendes.financesapi.exception.PasswordsDoNotMatchException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DisplayName("Unit tests for AuthenticationServiceImpl")
class AuthenticationServiceImplTest {

  @InjectMocks
  private AuthenticationServiceImpl authService;

  @Mock
  private AuthenticationManager authManagerMock;
  @Mock
  private TokenService tokenServiceMock;
  @Mock
  private UserService userServiceMock;

  @Nested
  class SignInMethod {

    @Test
    @DisplayName("signIn must return TokenResponse when sign in successfully")
    void signIn_MustReturnTokenResponse_WhenSignInSuccessfully() {
      BDDMockito.when(authManagerMock.authenticate(ArgumentMatchers.any(Authentication.class)))
          .thenReturn(new TestingAuthenticationToken("lorem@email.com", "1234567890"));
      BDDMockito.when(tokenServiceMock.generateToken(ArgumentMatchers.any(Authentication.class)))
          .thenReturn("deiud2iued297d8h8gc67934ybf08df65a9dft9w67fe0qw7higtyd4vijmplad.s9076213");

      SignInRequest requestBody = SignInRequest.builder()
          .email("lorem@email.com")
          .password("1234567890")
          .build();

      TokenResponse actualTokenResponse = authService.signIn(requestBody);

      Assertions.assertThat(actualTokenResponse).isNotNull();
      Assertions.assertThat(actualTokenResponse.getType()).isNotBlank().isEqualTo("Bearer");
      Assertions.assertThat(actualTokenResponse.getToken()).isNotBlank();
    }

    @Test
    @DisplayName("signIn must throws BadCredentialsException when credentials are invalid")
    void signIn_MustThrowsBadCredentialsException_WhenCredentialsAreInvalid() {
      BDDMockito.given(authManagerMock.authenticate(ArgumentMatchers.any(Authentication.class)))
          .willThrow(new BadCredentialsException("Bad Credentials"));

      SignInRequest requestBody = SignInRequest.builder()
          .email("lorem@email.com")
          .password("invalid_password")
          .build();

      Assertions.assertThatExceptionOfType(BadCredentialsException.class)
          .isThrownBy(() -> authService.signIn(requestBody))
          .withMessage("Bad Credentials");
    }

  }

  @Nested
  class RegisterMethod {

    @Test
    @DisplayName("register must return UserResponse when register successfully")
    void register_MustReturnUserResponse_WhenRegisterSuccessfully() {
      UserResponse userResponse = UserResponse.builder()
          .id(1000L)
          .name("Lorem Ipsum")
          .email("lorem@email.com")
          .build();

      BDDMockito.when(userServiceMock.createAccount(ArgumentMatchers.any(SignupRequest.class)))
          .thenReturn(userResponse);

      SignupRequest requestBody = SignupRequest.builder()
          .name("Lorem Ipsum")
          .email("lorem@email.com")
          .password("1234567890")
          .confirm("1234567890")
          .build();

      UserResponse actualUserResponse = authService.register(requestBody);

      Assertions.assertThat(actualUserResponse).isNotNull();
      Assertions.assertThat(actualUserResponse.getId()).isNotNull().isEqualTo(1000L);
      Assertions.assertThat(actualUserResponse.getName()).isNotBlank().isEqualTo("Lorem Ipsum");
      Assertions.assertThat(actualUserResponse.getEmail()).isNotBlank().isEqualTo("lorem@email.com");
    }

    @Test
    @DisplayName("register must throws PasswordsDoNotMatchException when passwords and confirm do not match")
    void register_MustThrowsPasswordsDoNotMatchException_WhenPasswordAndConfirmDoNotMatch() {
      BDDMockito.given(userServiceMock.createAccount(ArgumentMatchers.any(SignupRequest.class)))
          .willThrow(new PasswordsDoNotMatchException("Password and confirm do not match"));

      SignupRequest requestBody = SignupRequest.builder()
          .name("Lorem Ipsum")
          .email("lorem@email.com")
          .password("1234567890")
          .confirm("12345678")
          .build();

      Assertions.assertThatExceptionOfType(PasswordsDoNotMatchException.class)
          .isThrownBy(() -> authService.register(requestBody))
          .withMessage("Password and confirm do not match");
    }

    @Test
    @DisplayName("register must throws DataConflictException when email is alreay in use")
    void register_MustThrowsDataConflictException_WhenEmailIsAlreadyInUse() {
      BDDMockito.given(userServiceMock.createAccount(ArgumentMatchers.any(SignupRequest.class)))
          .willThrow(new DataConflictException("Email is already in use"));

      SignupRequest requestBody = SignupRequest.builder()
          .name("Lorem Ipsum")
          .email("lorem@email.com")
          .password("1234567890")
          .confirm("1234567890")
          .build();

      Assertions.assertThatExceptionOfType(DataConflictException.class)
          .isThrownBy(() -> authService.register(requestBody))
          .withMessage("Email is already in use");
    }

  }

}