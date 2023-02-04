package br.com.emendes.financesapi.controller.form;

import br.com.emendes.financesapi.model.entity.User;
import br.com.emendes.financesapi.validation.annotation.ValidPassword;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SignupForm {

  @Schema(example = "Mei")
  @NotBlank
  private String name;

  @NotBlank
  @Email
  private String email;

  @ValidPassword
  private String password;

  @NotBlank
  private String confirm;

  public User toUser() {
    return new User(name, email, password);
  }

  public boolean passwordMatch() {
    return this.password.equals(this.confirm);
  }

}
