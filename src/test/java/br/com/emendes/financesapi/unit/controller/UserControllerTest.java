package br.com.emendes.financesapi.unit.controller;

import br.com.emendes.financesapi.controller.UserController;
import br.com.emendes.financesapi.dto.request.ChangePasswordRequest;
import br.com.emendes.financesapi.dto.response.UserResponse;
import br.com.emendes.financesapi.exception.EntityNotFoundException;
import br.com.emendes.financesapi.service.UserService;
import br.com.emendes.financesapi.exception.PasswordsDoNotMatchException;
import br.com.emendes.financesapi.exception.WrongPasswordException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(controllers = {UserController.class}, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@DisplayName("Tests for UserController")
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private UserService userServiceMock;

  private static final String USER_BASE_URI = "/api/users";

  @Nested
  @DisplayName("Tests for readAll endpoint")
  class ReadAllEndpoint {

    @Test
    @DisplayName("readAll must return Page<UserResponse> when read all successfully")
    void readAll_MustReturnPageUserResponse_WhenReadAllSuccessfully() throws Exception {
      UserResponse userResponse = UserResponse.builder()
          .id(100L)
          .name("Lorem Ipsum")
          .email("lorem@email.com")
          .build();

      BDDMockito.when(userServiceMock.read(any()))
          .thenReturn(new PageImpl<>(List.of(userResponse)));

      mockMvc.perform(get(USER_BASE_URI))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.totalElements").value(1))
          .andExpect(jsonPath("$.content[0].id").value(100L))
          .andExpect(jsonPath("$.content[0].name").value("Lorem Ipsum"))
          .andExpect(jsonPath("$.content[0].email").value("lorem@email.com"));
    }

  }

  @Nested
  @DisplayName("Tests for delete endpoint")
  class DeleteEndpoint {

    @Test
    @DisplayName("delete must return status 204 when delete successfully")
    void delete_MustReturnStatus204_WhenDeleteSuccessfully() throws Exception {
      mockMvc.perform(delete(USER_BASE_URI + "/100"))
          .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("delete must return ProblemDetail when not found user")
    void delete_MustReturnProblemDetail_WhenNotFoundUser() throws Exception {
      BDDMockito.willThrow(new EntityNotFoundException("User not found with id 999"))
          .given(userServiceMock).delete(999L);

      mockMvc.perform(delete(USER_BASE_URI + "/999"))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.title").value("Entity not found"))
          .andExpect(jsonPath("$.detail").value("User not found with id 999"));
    }

    @Test
    @DisplayName("delete must return ProblemDetail when id is invalid")
    void delete_MustReturnProblemDetail_WhenIdIsInvalid() throws Exception {
      mockMvc.perform(delete(USER_BASE_URI + "/1o0"))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.title").value("Type mismatch"))
          .andExpect(jsonPath("$.detail").value("An error occurred trying to cast String to Number"));
    }

  }

  @Nested
  @DisplayName("Tests for changePassword endpoint")
  class ChangePasswordEndpoint {

    private final String CONTENT_TYPE = "application/json;charset=UTF-8";

    @Test
    @DisplayName("changePassword must return status 204 when change password successfully")
    void changePassword_MustReturnStatus204_WhenChangePasswordSuccessfully() throws Exception {
      String requestBody = """
          {
            "oldPassword" : "12345678",
            "newPassword" : "1234567890",
            "confirm" : "1234567890"
          }
          """;
      mockMvc.perform(put(USER_BASE_URI + "/password").contentType(CONTENT_TYPE).content(requestBody))
          .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("changePassword must return ValidationProblemDetail when request body is invalid")
    void changePassword_MustReturnValidationProblemDetail_WhenRequestBodyIsInvalid() throws Exception {
      String requestBody = """
          {
            "oldPassword" : "12345678",
            "newPassword" : "",
            "confirm" : "1234567890"
          }
          """;
      mockMvc.perform(put(USER_BASE_URI + "/password").contentType(CONTENT_TYPE).content(requestBody))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.title").value("Invalid fields"))
          .andExpect(jsonPath("$.detail").value("Some fields are invalid"))
          .andExpect(jsonPath("$.fields").isString())
          .andExpect(jsonPath("$.messages").isString());
    }

    @Test
    @DisplayName("changePassword must return ProblemDetail when newPassword and confirm do not match")
    void changePassword_MustReturnProblemDetail_WhenNewPasswordAndConfirmDoNotMatch() throws Exception {
      BDDMockito.willThrow(new PasswordsDoNotMatchException("Passwords do not match"))
          .given(userServiceMock).changePassword(any(ChangePasswordRequest.class));

      String requestBody = """
          {
            "oldPassword" : "12345678",
            "newPassword" : "1234567890",
            "confirm" : "1234567"
          }
          """;
      mockMvc.perform(put(USER_BASE_URI + "/password").contentType(CONTENT_TYPE).content(requestBody))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.title").value("Passwords do not match"))
          .andExpect(jsonPath("$.detail").value("Passwords do not match"));
    }

    @Test
    @DisplayName("changePassword must return ProblemDetail when old password is wrong")
    void changePassword_MustReturnProblemDetail_WhenOldPasswordIsWrong() throws Exception {
      BDDMockito.willThrow(new WrongPasswordException("Wrong password"))
          .given(userServiceMock).changePassword(any(ChangePasswordRequest.class));

      String requestBody = """
          {
            "oldPassword" : "12345678adidaisdb",
            "newPassword" : "1234567890",
            "confirm" : "1234567890"
          }
          """;
      mockMvc.perform(put(USER_BASE_URI + "/password").contentType(CONTENT_TYPE).content(requestBody))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.title").value("Wrong password"))
          .andExpect(jsonPath("$.detail").value("Wrong password"));
    }

  }

}
