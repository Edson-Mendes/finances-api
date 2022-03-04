package br.com.emendes.financesapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.emendes.financesapi.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{

}
