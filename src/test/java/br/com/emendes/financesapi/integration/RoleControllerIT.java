package br.com.emendes.financesapi.integration;

import br.com.emendes.financesapi.controller.dto.RoleDto;
import br.com.emendes.financesapi.controller.dto.TokenDto;
import br.com.emendes.financesapi.controller.dto.error.ErrorDto;
import br.com.emendes.financesapi.controller.form.LoginForm;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@DisplayName("Integration tests for /roles/**")
class RoleControllerIT {

  @Autowired
  private TestRestTemplate testRestTemplate;

  private final String BASE_URI = "/roles";
  private final HttpHeaders HEADERS_ADMIN = new HttpHeaders();
  private final HttpHeaders HEADERS_USER = new HttpHeaders();

  @BeforeAll
  public void singInAndAddAuthorizationHeader() {
    String adminEmail = "admin@email.com";
    String adminPassword = "123456";

    ResponseEntity<TokenDto> responseAdmin = requestToSignIn(new LoginForm(adminEmail, adminPassword));

    HEADERS_ADMIN.add("Authorization", "Bearer " + responseAdmin.getBody().getToken());

    String userEmail = "user@email.com";
    String userPassword = "123456";

    ResponseEntity<TokenDto> responseUser = requestToSignIn(new LoginForm(userEmail, userPassword));

    HEADERS_USER.add("Authorization", "Bearer " + responseUser.getBody().getToken());
  }

  @Test
  @DisplayName("readAll must returns List<RoleDto> when user is a ADMIN")
  void readAll_ReturnsListRoleDto_WhenUserIsAAdmin() {

    HttpEntity<Void> requestEntity = new HttpEntity<>(HEADERS_ADMIN);

    ResponseEntity<List<RoleDto>> response = testRestTemplate
        .exchange(BASE_URI, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {
        });

    HttpStatus statusCode = response.getStatusCode();
    List<RoleDto> responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(responseBody)
        .isNotNull()
        .hasSize(2)
        .contains(new RoleDto(1L, "ROLE_USER"))
        .contains(new RoleDto(2L, "ROLE_ADMIN"));
  }

  @Test
  @DisplayName("readAll must returns status 403 when role is user")
  void readAll_ReturnsStatus403_WhenRoleIsUser(){
    HttpEntity<Void> requestEntity = new HttpEntity<>(HEADERS_USER);
    ResponseEntity<Void> response = testRestTemplate
        .exchange(BASE_URI, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>(){});

    HttpStatus statusCode = response.getStatusCode();

    Assertions.assertThat(statusCode).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  @DisplayName("readById must returns RoleDto when user is a ADMIN and id is valid")
  void readById_ReturnsListRoleDto_WhenUserIsAAdminAndIdIsValid() {

    HttpEntity<Void> requestEntity = new HttpEntity<>(HEADERS_ADMIN);

    ResponseEntity<RoleDto> response = testRestTemplate
        .exchange(BASE_URI+"/1", HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {
        });

    HttpStatus statusCode = response.getStatusCode();
    RoleDto responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(responseBody)
        .isNotNull()
        .isEqualTo(new RoleDto(1L, "ROLE_USER"));
  }

  @Test
  @DisplayName("readById must returns status 403 when role is user")
  void readById_ReturnsStatus403_WhenRoleIsUser(){
    HttpEntity<Void> requestEntity = new HttpEntity<>(HEADERS_USER);
    ResponseEntity<Void> response = testRestTemplate
        .exchange(BASE_URI+"/1", HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>(){});

    HttpStatus statusCode = response.getStatusCode();

    Assertions.assertThat(statusCode).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  @DisplayName("readById must returns status 404 when id not exists")
  void readById_ReturnsStatus404_WhenIdNotExists(){
    HttpEntity<Void> requestEntity = new HttpEntity<>(HEADERS_ADMIN);

    ResponseEntity<ErrorDto> response = testRestTemplate
        .exchange(BASE_URI + "/10000", HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});

    HttpStatus statusCode = response.getStatusCode();
    ErrorDto responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualTo(HttpStatus.NOT_FOUND);
    Assertions.assertThat(responseBody).isNotNull();
    Assertions.assertThat(responseBody.getError()).isEqualTo("Not Found");
    Assertions.assertThat(responseBody.getMessage()).contains("não existe role com id: ");
  }

  private ResponseEntity<TokenDto> requestToSignIn(LoginForm loginForm) {
    HttpEntity<LoginForm> requestBodyAdmin = new HttpEntity<>(loginForm);

    return testRestTemplate.exchange(
        "/auth/signin", HttpMethod.POST, requestBodyAdmin, new ParameterizedTypeReference<>() {
        });
  }
}