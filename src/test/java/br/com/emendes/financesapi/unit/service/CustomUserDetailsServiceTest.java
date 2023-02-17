package br.com.emendes.financesapi.unit.service;

import br.com.emendes.financesapi.config.security.service.CustomUserDetailsService;
import br.com.emendes.financesapi.model.entity.Role;
import br.com.emendes.financesapi.model.entity.User;
import br.com.emendes.financesapi.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests for CustomUserDetailsService")
class CustomUserDetailsServiceTest {

  @InjectMocks
  private CustomUserDetailsService customUserDetailsService;

  @Mock
  private UserRepository userRepositoryMock;

  private final User USER = User.builder()
      .email("lorem@email.com")
      .password("1234567890")
      .id(100L)
      .name("Lorem Ipsum")
      .roles(List.of(Role.builder().id(1).name("ROLE_USER").build()))
      .build();

  @Test
  @DisplayName("loadUserByUsername must returns UserDetails when successful")
  void loadUserByUsername_ReturnsUserDetails_WhenSuccessful() {
    BDDMockito.when(userRepositoryMock.findByEmail("lorem@email.com"))
        .thenReturn(Optional.of(USER));

    UserDetails userDetails = customUserDetailsService.loadUserByUsername("lorem@email.com");

    Assertions.assertThat(userDetails).isNotNull();
    Assertions.assertThat(userDetails.getUsername()).isEqualTo("lorem@email.com");
  }

  @Test
  @DisplayName("loadUserByUsername must throw UsernameNotFoundException when is not successful")
  void loadUserByUsername_ThrowsUsernameNotFoundException_WhenIsNotSuccessful() {
    BDDMockito.when(userRepositoryMock.findByEmail("notexists@email.com"))
        .thenReturn(Optional.empty());

    Assertions.assertThatExceptionOfType(UsernameNotFoundException.class)
        .isThrownBy(() -> customUserDetailsService.loadUserByUsername("notexists@email.com"))
        .withMessage("User not found");
  }

}
