package br.com.emendes.financesapi.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "tb_user")
public class User implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false, length = 100)
  private String name;
  @Column(unique = true, nullable = false, length = 150)
  private String email;
  @Column(nullable = false)
  private String password;

  @ManyToMany(fetch = FetchType.EAGER)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private List<Role> roles = new ArrayList<>();

  public User(String name, String email, String password) {
    this.name = name;
    this.email = email;
    this.password = new BCryptPasswordEncoder().encode(password);
    this.roles.add(new Role(1, "ROLE_USER"));
  }

  public User(Long id) {
    this.id = id;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return this.roles;
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  @Override
  public String getUsername() {
    return this.email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  public List<Role> getRoles() {
    return Collections.unmodifiableList(roles);
  }

  public void setRoles(List<Role> roles) {
    this.roles = roles;
  }
}
