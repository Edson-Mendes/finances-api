package br.com.emendes.financesapi.unit.controller;

import br.com.emendes.financesapi.controller.AuthenticationController;
import br.com.emendes.financesapi.dto.request.SignInRequest;
import br.com.emendes.financesapi.dto.request.SignupRequest;
import br.com.emendes.financesapi.dto.response.TokenResponse;
import br.com.emendes.financesapi.dto.response.UserResponse;
import br.com.emendes.financesapi.service.AuthenticationService;
import br.com.emendes.financesapi.validation.exception.DataConflictException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ActiveProfiles(value = "test")
@WebMvcTest(value = AuthenticationController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@DisplayName("Tests for AuthenticationController")
class AuthenticationControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private AuthenticationService authServiceMock;

  private final String AUTH_BASE_URI = "/api/auth";

  @Nested
  @DisplayName("Tests for SignIn endpoint")
  class signInEndpoint {

    private final String URI = AUTH_BASE_URI + "/signin";

    @Test
    @DisplayName("signIn must return TokenResponse when sign in successfully")
    void signIn_MustReturnTokenResponse_WhenSignInSuccessfully() throws Exception {
      TokenResponse tokenResponse = TokenResponse.builder()
          .type("Bearer")
          .token("dfuehf08743hf8374hf80he8f9ahsd9fasgdf976gsda7f9gsa7gfsa76dgf9asgfd97sagfs")
          .build();

      BDDMockito.when(authServiceMock.signIn(ArgumentMatchers.any(SignInRequest.class)))
          .thenReturn(tokenResponse);

      String requestBody = """
          {
            "email" : "lorem@email.com",
            "password" : "1234567890"
          }
          """;

      mockMvc.perform(
              post(URI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andExpect(MockMvcResultMatchers.jsonPath("$.token").isNotEmpty())
          .andExpect(MockMvcResultMatchers.jsonPath("$.type").value("Bearer"));
    }

    @Test
    @DisplayName("signIn must return ErrorResponse when credentials are invalid")
    void signIn_MustReturnErrorResponse_WhenCredentialsAreInvalid() throws Exception {
      BDDMockito.given(authServiceMock.signIn(ArgumentMatchers.any(SignInRequest.class)))
          .willThrow(new BadCredentialsException("Bad Credentials"));

      String requestBody = """
          {
            "email" : "lorem@email.com",
            "password" : "invalid_password"
          }
          """;

      mockMvc.perform(
              post(URI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
          .andExpect(MockMvcResultMatchers.status().isBadRequest())
          .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Bad credentials"))
          .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Invalid email or password"));
    }

  }

  @Nested
  @DisplayName("Tests for SignUp endpoint")
  class signUpEndpoint {

    private final String URI = AUTH_BASE_URI + "/signup";

    @Test
    @DisplayName("signup must return UserResponse when sign up successfully")
    void signUp_MustReturnUserResponse_WhenSignUnSuccessfully() throws Exception {
      UserResponse userResponse = UserResponse.builder()
          .id(1000L)
          .email("lorem@email.com")
          .name("Lorem Ipsum")
          .build();

      BDDMockito.when(authServiceMock.register(ArgumentMatchers.any(SignupRequest.class)))
          .thenReturn(userResponse);

      String requestBody = """
          {
            "name" : "Lorem Ipsum",
            "email" : "lorem@email.com",
            "password" : "1234567890",
            "confirm" : "1234567890"
          }
          """;

      mockMvc.perform(
              post(URI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
          .andExpect(MockMvcResultMatchers.status().isCreated())
          .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1000L))
          .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Lorem Ipsum"))
          .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("lorem@email.com"));
    }

    @Test
    @DisplayName("signup must return List<FormErrorDto> when request body is invalid")
    void signUp_MustReturnListFormErrorDto_WhenRequestBodyIsInvalid() throws Exception {
      String requestBody = """
          {
            "name" : "",
            "email" : "lorememailcom",
            "password" : "1234567890",
            "confirm" : "1234567890"
          }
          """;

      // TODO: a resposta para request body inválido será alterado, então deve ser adicionado as assertivas.
      mockMvc.perform(
              post(URI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("signUp must return ErrorResponse when informed email is already in use")
    void signUp_MustReturnErrorResponse_WhenInformedEmailIsAlreadyInUse() throws Exception {
      BDDMockito.given(authServiceMock.register(ArgumentMatchers.any(SignupRequest.class)))
          .willThrow(new DataConflictException("Email conflict"));

      String requestBody = """
          {
            "name" : "Lorem Ipsum",
            "email" : "lorem@email.com",
            "password" : "1234567890",
            "confirm" : "1234567890"
          }
          """;

      // TODO: adicionar as assertivas depois que ErrorResponse for alterado.
      mockMvc.perform(
              post(URI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
          .andExpect(MockMvcResultMatchers.status().isConflict())
          .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("CONFLICT"))
          .andExpect(MockMvcResultMatchers.jsonPath("$.message").isNotEmpty());
    }

  }

}
