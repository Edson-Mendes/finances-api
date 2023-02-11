package br.com.emendes.financesapi.dto.request;

import br.com.emendes.financesapi.model.entity.User;
import br.com.emendes.financesapi.validation.annotation.NoWhiteSpace;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SignupRequest {

  @NotBlank(message = "name must not be null or blank")
  @Size(min = 1, max = 100, message = "name must contain max {max} characters")
  private String name;

  @NotBlank(message = "email must not be null or blank")
  @Size(min = 1, max = 150, message = "email must contain max {max} characters")
  @Email(message = "must be a well formed email")
  private String email;

  @NoWhiteSpace(message = "password must not contains whitespace, tab or newline")
  @NotBlank(message = "password must not be null or blank")
  @Size(min = 8, max = 30, message = "must contain between {min} and {max} characters")
  private String password;

  @NotBlank(message = "confirm must not be null or blank")
  private String confirm;

  public User toUser() {
    return new User(name, email, password);
  }

  public boolean passwordMatch() {
    return this.password.equals(this.confirm);
  }

}
