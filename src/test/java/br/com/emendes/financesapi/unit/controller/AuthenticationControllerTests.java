package br.com.emendes.financesapi.unit.controller;

import br.com.emendes.financesapi.controller.AuthenticationController;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.emendes.financesapi.controller.dto.TokenDto;
import br.com.emendes.financesapi.controller.form.LoginForm;
import br.com.emendes.financesapi.creator.LoginFormCreator;
import br.com.emendes.financesapi.service.TokenService;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests for AuthenticationController")
public class AuthenticationControllerTests {

  @InjectMocks
  private AuthenticationController authenticationController;

  @Mock
  private AuthenticationManager authManagerMock;
  @Mock
  private TokenService tokenServiceMock;

  private final String TOKEN = "thisIsAFakeToken12345";

  @BeforeEach
  public void setUp() {
    LoginForm loginForm = LoginFormCreator.validLoginForm();
    BDDMockito.when(authManagerMock.authenticate(ArgumentMatchers.any(loginForm.converter().getClass())))
        .thenReturn(loginForm.converter());

    BDDMockito.when(tokenServiceMock.generateToken(ArgumentMatchers.any(loginForm.converter().getClass())))
        .thenReturn(TOKEN);

  }

  @Test
  @DisplayName("auth must return ResponseEntity<TokenDto> when successful")
  void auth_ReturnsResponseEntityTokenDto_WhenSuccessful() {
    LoginForm loginForm = LoginFormCreator.validLoginForm();
    ResponseEntity<TokenDto> responseEntity = authenticationController.auth(loginForm);

    Assertions.assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);
    Assertions.assertThat(responseEntity.getBody().getToken()).isEqualTo(TOKEN);
  }
}
