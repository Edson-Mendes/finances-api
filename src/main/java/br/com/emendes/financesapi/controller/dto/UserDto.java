package br.com.emendes.financesapi.controller.dto;

import br.com.emendes.financesapi.model.User;

public class UserDto {

  private Long id;
  private String name;
  private String email;

  public UserDto(User user) {
    this.id = user.getId();
    this.name = user.getName();
    this.email = user.getEmail();
  }

  public UserDto(Long id, String name, String email) {
    this.id = id;
    this.name = name;
    this.email = email;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @Override
  public boolean equals(Object obj) {
    if(this == obj){
      return true;
    }
    if (obj == null || obj.getClass() != getClass()) {
      return false;
    }
    UserDto other = (UserDto) obj;
    return this.id == other.getId() && this.name.equals(other.getName()) && this.email.equals(other.getEmail());
  }

  @Override
  public int hashCode() {
    int result = 17;
    if(id != null){
      result = result * 31 + id.hashCode();
    }
    if(name != null){
      result = result * 31 + name.hashCode();
    }
    if(email != null){
      result = result * 31 + email.hashCode();
    }

    return result;
  }

}
