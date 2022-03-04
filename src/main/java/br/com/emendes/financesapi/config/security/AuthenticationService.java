package br.com.emendes.financesapi.config.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.emendes.financesapi.model.User;
import br.com.emendes.financesapi.repository.UserRepository;

@Service
public class AuthenticationService implements UserDetailsService{

  @Autowired
  private UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Optional<User> user = userRepository.findByEmail(email);
    if(user.isPresent()){
      return user.get();
    }
    throw new UsernameNotFoundException("Dados inválidos!");
  }
  
}
