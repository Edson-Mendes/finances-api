package br.com.emendes.financesapi.controller;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.emendes.financesapi.controller.dto.UserDto;
import br.com.emendes.financesapi.controller.form.ChangePasswordForm;
import br.com.emendes.financesapi.service.TokenService;
import br.com.emendes.financesapi.service.UserService;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests for UserController")
public class UserControllerTests {

  @InjectMocks
  private UserController userController;

  @Mock
  private TokenService tokenServiceMock;

  @Mock
  private UserService userServiceMock;

  private final Pageable PAGEABLE = PageRequest.of(0, 10, Direction.ASC, "id");
  private final UserDto USER_DTO = new UserDto(1000l, "Lorem Ipsum", "lorem@email.com");

  @BeforeEach
  public void setUp() {
    Page<UserDto> pageUserDto = new PageImpl<>(List.of(USER_DTO));
    ChangePasswordForm changeForm = new ChangePasswordForm("123456789O", "123456789O");

    BDDMockito.when(userServiceMock.read(PAGEABLE))
        .thenReturn(pageUserDto);

    BDDMockito.doNothing().when(userServiceMock).delete(1000l);
    BDDMockito.doNothing().when(userServiceMock).changePassword(changeForm, 1000l);
    ;
  }

  @Test
  @DisplayName("read must returns ResponseEntity<Page<UserDto>> when successful")
  void read_ReturnsResponseEntityPageUserDto_WhenSuccessful() {
    ResponseEntity<Page<UserDto>> response = userController.read(PAGEABLE);

    Assertions.assertThat(response.getStatusCodeValue()).isEqualTo(200);
    Assertions.assertThat(response.getBody().getContent().get(0).getEmail()).isEqualTo("lorem@email.com");
    Assertions.assertThat(response.getBody().getContent().get(0).getName()).isEqualTo("Lorem Ipsum");
  }

  @Test
  @DisplayName("delete must returns ResponseEntity<Void> when successful")
  void delete_ReturnsResponseEntityVoid_WhenSuccessful() {
    ResponseEntity<Void> response = userController.delete(1000l);

    Assertions.assertThat(response.getStatusCodeValue()).isEqualTo(204);
  }
}
