package br.com.emendes.financesapi.config.security.service;

import br.com.emendes.financesapi.model.entity.User;
import br.com.emendes.financesapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementação de {@link UserDetailsService}.
 */
@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String email) {
    Optional<User> user = userRepository.findByEmail(email);

    return user.orElseThrow(() -> {
      throw new UsernameNotFoundException("User not found");
    });
  }

}
