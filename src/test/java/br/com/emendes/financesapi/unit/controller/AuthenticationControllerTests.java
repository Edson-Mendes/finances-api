package br.com.emendes.financesapi.unit.controller;

import br.com.emendes.financesapi.controller.AuthenticationController;
import br.com.emendes.financesapi.controller.dto.TokenDto;
import br.com.emendes.financesapi.controller.dto.UserDto;
import br.com.emendes.financesapi.controller.form.LoginForm;
import br.com.emendes.financesapi.controller.form.SignupForm;
import br.com.emendes.financesapi.service.impl.SigninServiceImpl;
import br.com.emendes.financesapi.service.impl.UserServiceImpl;
import br.com.emendes.financesapi.util.creator.LoginFormCreator;
import br.com.emendes.financesapi.util.creator.SignupFormCreator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.util.UriComponentsBuilder;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests for AuthenticationController")
class AuthenticationControllerTests {

  @InjectMocks
  private AuthenticationController authenticationController;
  @Mock
  private SigninServiceImpl signinServiceImplMock;
  @Mock
  private UserServiceImpl userServiceImplImplMock;

  private final LoginForm VALID_LOGIN_FORM = LoginFormCreator.validLoginForm();
  private final UriComponentsBuilder URI_BUILDER = UriComponentsBuilder.fromHttpUrl("http://localhost:8080");

  @BeforeEach
  public void setUp() {
    UserDto userDto = new UserDto(55L, "Lorem Ipsum", "lorem@email.com");
    BDDMockito.when(signinServiceImplMock.login(VALID_LOGIN_FORM))
        .thenReturn(new TokenDto("thisIsAFakeToken12345", "Bearer"));

    BDDMockito.when(userServiceImplImplMock.createAccount(ArgumentMatchers.any(SignupForm.class)))
        .thenReturn(userDto);

  }

  @Test
  @DisplayName("auth must returns ResponseEntity<TokenDto> when successful")
  void auth_ReturnsResponseEntityTokenDto_WhenSuccessful() {
    ResponseEntity<TokenDto> response = authenticationController.auth(VALID_LOGIN_FORM);

    HttpStatus statusCode = response.getStatusCode();
    TokenDto responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.OK);
    Assertions.assertThat(responseBody).isNotNull();
    Assertions.assertThat(responseBody.getType()).isEqualTo("Bearer");
    Assertions.assertThat(responseBody.getToken()).isEqualTo("thisIsAFakeToken12345");
  }

  @Test
  @DisplayName("register must returns ResponseEntity<UserDto> when created successful")
  void register_ReturnsResponseEntityUserDto_WhenCreatedSuccessful(){
    SignupForm signupForm = SignupFormCreator.validSignupForm();

    ResponseEntity<UserDto> response = authenticationController.register(signupForm, URI_BUILDER);

    HttpStatus statusCode = response.getStatusCode();
    UserDto responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.CREATED);
    Assertions.assertThat(responseBody).isNotNull();
    Assertions.assertThat(responseBody.getId()).isEqualTo(55L);
    Assertions.assertThat(responseBody.getEmail()).isEqualTo("lorem@email.com");
    Assertions.assertThat(responseBody.getName()).isEqualTo("Lorem Ipsum");
  }
}
