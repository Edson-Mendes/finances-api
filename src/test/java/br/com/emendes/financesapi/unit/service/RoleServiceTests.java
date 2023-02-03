package br.com.emendes.financesapi.unit.service;

import java.util.List;
import java.util.Optional;

import javax.persistence.NoResultException;

import br.com.emendes.financesapi.service.RoleService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.emendes.financesapi.controller.dto.RoleDto;
import br.com.emendes.financesapi.model.entity.Role;
import br.com.emendes.financesapi.repository.RoleRepository;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests for RoleService")
public class RoleServiceTests {

  @InjectMocks
  private RoleService roleService;

  @Mock
  private RoleRepository roleRepositoryMock;

  private final Role ROLE_USER = new Role(1, "ROLE_USER");
  private final Role ROLE_ADMIN = new Role(2, "ROLE_ADMIN");

  @BeforeEach
  public void setUp() {
    List<Role> listRoles = List.of(ROLE_USER, ROLE_ADMIN);

    BDDMockito.when(roleRepositoryMock.findAll())
        .thenReturn(listRoles);

    BDDMockito.when(roleRepositoryMock.findById(1l))
        .thenReturn(Optional.of(ROLE_USER));

    BDDMockito.when(roleRepositoryMock.findById(1000l))
        .thenReturn(Optional.empty());

  }

  @Test
  @DisplayName("readAll must return List of RoleDto when successful")
  void readAll_ReturnsListOfRoleDto_WhenSuccessful() {

    List<RoleDto> listRoleDto = this.roleService.readAll();

    Assertions.assertThat(listRoleDto)
        .isNotEmpty()
        .hasSize(2);
  }

  @Test
  @DisplayName("readById must return RoleDto when successful")
  void readById_ReturnsRoleDto_WhenSuccessful() {
    Long id = 1l;

    RoleDto roleDto = this.roleService.readById(id);

    Assertions.assertThat(roleDto).isNotNull();
    Assertions.assertThat(roleDto.getId()).isEqualTo(id);
    Assertions.assertThat(roleDto.getName()).isEqualTo(ROLE_USER.getName());
  }

  @Test
  @DisplayName("readById must return RoleDto when successful")
  void readById_ThrowsNoResultException_WhenNotFoundRole() {
    Long id = 1000l;

    Assertions.assertThatExceptionOfType(NoResultException.class)
        .isThrownBy(() -> this.roleService.readById(id))
        .withMessageContaining("não existe role com id: ");
  }

}
