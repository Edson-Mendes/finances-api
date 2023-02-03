package br.com.emendes.financesapi.unit.service;

import java.util.Optional;

import br.com.emendes.financesapi.service.AuthenticationService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.emendes.financesapi.util.creator.UserCreator;
import br.com.emendes.financesapi.model.entity.User;
import br.com.emendes.financesapi.repository.UserRepository;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests for AuthenticationService")
public class AuthenticationServiceTests {

  @InjectMocks
  private AuthenticationService authenticationService;

  @Mock
  private UserRepository userRepositoryMock;

  private final String EMAIL_NOT_EXISTING = "notexists@email.com";

  @BeforeEach
  public void setUp() {
    User validUser = UserCreator.validUserForExpenseRepositoryTest();

    BDDMockito.when(userRepositoryMock.findByEmail(validUser.getEmail()))
        .thenReturn(Optional.of(validUser));

    BDDMockito.when(userRepositoryMock.findByEmail(EMAIL_NOT_EXISTING))
        .thenReturn(Optional.empty());
  }

  @Test
  @DisplayName("loadUserByUsername must returns UserDetails when successful")
  void loadUserByUsername_ReturnsUserDetails_WhenSuccessful() {
    String email = UserCreator.validUserForExpenseRepositoryTest().getEmail();

    UserDetails userDetails = authenticationService.loadUserByUsername(email);

    Assertions.assertThat(userDetails).isNotNull();
    Assertions.assertThat(userDetails.getUsername()).isEqualTo(email);
  }

  @Test
  @DisplayName("loadUserByUsername must throw UsernameNotFoundException when is not successful")
  void loadUserByUsername_ThrowsUsernameNotFoundException_WhenIsNotSuccessful() {
    String email = EMAIL_NOT_EXISTING;

    Assertions.assertThatExceptionOfType(UsernameNotFoundException.class)
        .isThrownBy(() -> authenticationService.loadUserByUsername(email))
        .withMessage("Dados inválidos!");
  }

}
