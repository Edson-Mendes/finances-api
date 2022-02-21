package br.com.emendes.financesapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.emendes.financesapi.model.User;

public interface UserRepository extends JpaRepository<User, Long>{

  Optional<User> findByEmail(String email);
  
}
