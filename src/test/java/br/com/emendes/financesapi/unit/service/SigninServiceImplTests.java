package br.com.emendes.financesapi.unit.service;

import br.com.emendes.financesapi.dto.response.TokenResponse;
import br.com.emendes.financesapi.dto.request.SignInRequest;
import br.com.emendes.financesapi.service.impl.SigninServiceImpl;
import br.com.emendes.financesapi.config.security.service.TokenServiceImpl;
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
class SigninServiceImplTests {

  @InjectMocks
  private SigninServiceImpl signinServiceImpl;
  @Mock
  private AuthenticationManager authManagerMock;
  @Mock
  private TokenServiceImpl tokenServiceImplMock;

  @BeforeEach
  public void setUp() {
    SignInRequest signInRequest = LoginFormCreator.validLoginForm();
    BDDMockito.when(authManagerMock.authenticate(ArgumentMatchers.any(signInRequest.converter().getClass())))
        .thenReturn(signInRequest.converter());

    String token = "thisIsAFakeToken12345";
    BDDMockito.when(tokenServiceImplMock.generateToken(ArgumentMatchers.any(signInRequest.converter().getClass())))
        .thenReturn(token);

  }

  @Test
  @DisplayName("login must returns TokenDto when successful")
  void login_ReturnsTokenDto_WhenSucessful(){
    SignInRequest signInRequest = new SignInRequest("user@email.com", "123456");

    TokenResponse tokenResponse = signinServiceImpl.login(signInRequest);

    Assertions.assertThat(tokenResponse).isNotNull();
    Assertions.assertThat(tokenResponse.getType()).isEqualTo("Bearer");
    Assertions.assertThat(tokenResponse.getToken()).isEqualTo("thisIsAFakeToken12345");
  }

  @Test
  @DisplayName("login must throws BadCredentialsException when credentials are invalid")
  void login_ThrowsBadCredentialsException_WhenCredentialsAreInvalid(){
    SignInRequest signInRequest = new SignInRequest("user@email.com", "wrong_password");

    BDDMockito.willThrow(BadCredentialsException.class)
        .given(authManagerMock).authenticate(ArgumentMatchers.any(signInRequest.converter().getClass()));

    Assertions.assertThatExceptionOfType(BadCredentialsException.class)
        .isThrownBy(() -> signinServiceImpl.login(signInRequest));

  }
}