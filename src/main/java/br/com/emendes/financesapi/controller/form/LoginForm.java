package br.com.emendes.financesapi.controller.form;

import lombok.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LoginForm {

  @NotBlank
  @Email(message = "deve ser um e-mail bem formado")
  private String email;

  @NotBlank
  private String password;

  // FIXME: ISSO NÃO DEVERIA ESTAR AQUI!
  public UsernamePasswordAuthenticationToken converter() {
    return new UsernamePasswordAuthenticationToken(email, password);
  }

}
