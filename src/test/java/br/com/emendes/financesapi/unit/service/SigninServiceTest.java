package br.com.emendes.financesapi.unit.service;

import br.com.emendes.financesapi.controller.dto.TokenDto;
import br.com.emendes.financesapi.controller.form.LoginForm;
import br.com.emendes.financesapi.service.SigninService;
import br.com.emendes.financesapi.service.TokenService;
import br.com.emendes.financesapi.util.creator.LoginFormCreator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DisplayName("Unit tests for SigninService")
class SigninServiceTest {

  @InjectMocks
  private SigninService signinService;
  @Mock
  private AuthenticationManager authManagerMock;
  @Mock
  private TokenService tokenServiceMock;

  @BeforeEach
  public void setUp() {
    LoginForm loginForm = LoginFormCreator.validLoginForm();
    BDDMockito.when(authManagerMock.authenticate(ArgumentMatchers.any(loginForm.converter().getClass())))
        .thenReturn(loginForm.converter());

    String token = "thisIsAFakeToken12345";
    BDDMockito.when(tokenServiceMock.generateToken(ArgumentMatchers.any(loginForm.converter().getClass())))
        .thenReturn(token);

  }

  @Test
  @DisplayName("login must returns TokenDto when successful")
  void login_ReturnsTokenDto_WhenSucessful(){
    LoginForm loginForm = new LoginForm("user@email.com", "123456");

    TokenDto tokenDto = signinService.login(loginForm);

    Assertions.assertThat(tokenDto).isNotNull();
    Assertions.assertThat(tokenDto.getType()).isEqualTo("Bearer");
    Assertions.assertThat(tokenDto.getToken()).isEqualTo("thisIsAFakeToken12345");
  }

  @Test
  @DisplayName("login must throws BadCredentialsException when credentials are invalid")
  void login_ThrowsBadCredentialsException_WhenCredentialsAreInvalid(){
    LoginForm loginForm = new LoginForm("user@email.com", "wrong_password");

    BDDMockito.willThrow(BadCredentialsException.class)
        .given(authManagerMock).authenticate(ArgumentMatchers.any(loginForm.converter().getClass()));

    Assertions.assertThatExceptionOfType(BadCredentialsException.class)
        .isThrownBy(() -> signinService.login(loginForm));

  }
}