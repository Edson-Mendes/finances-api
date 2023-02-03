package br.com.emendes.financesapi.unit.controller;

import java.util.List;

import br.com.emendes.financesapi.controller.RoleController;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.emendes.financesapi.controller.dto.RoleDto;
import br.com.emendes.financesapi.service.RoleService;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests for RoleController")
class RoleControllerTests {

  @InjectMocks
  private RoleController roleController;

  @Mock
  private RoleService roleServiceMock;

  private final RoleDto ROLE_USER = new RoleDto(1, "ROLE_USER");
  private final RoleDto ROLE_ADMIN = new RoleDto(2, "ROLE_ADMIN");
  private final RoleDto ROLE_MANAGER = new RoleDto(3, "ROLE_MANAGER");

  @BeforeEach
  public void setUp() {
    BDDMockito.when(roleServiceMock.readAll())
        .thenReturn(List.of(ROLE_USER, ROLE_ADMIN, ROLE_MANAGER));

    BDDMockito.when(roleServiceMock.readById(2L))
        .thenReturn(ROLE_ADMIN);
  }

  @Test
  @DisplayName("readAll must return ResponseEntity<List<RoleDto>> when successful")
  void readAll_ReturnsResponseEntityListRoleDto_WhenSuccessful() {

    ResponseEntity<List<RoleDto>> response = roleController.readAll();

    Assertions.assertThat(response.getStatusCodeValue()).isEqualTo(200);
    Assertions.assertThat(response.getBody()).hasSize(3);
  }

  @Test
  @DisplayName("readById must return ResponseEntity<RoleDto> when successful")
  void readById_ReturnsResponseEntityRoleDto_WhenSuccessful() {

    ResponseEntity<RoleDto> response = roleController.readById(2L);

    HttpStatus statusCode = response.getStatusCode();
    RoleDto responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(responseBody).isNotNull();
    Assertions.assertThat(responseBody.getId()).isEqualTo(ROLE_ADMIN.getId());
    Assertions.assertThat(responseBody.getName()).isEqualTo(ROLE_ADMIN.getName());
  }

}
