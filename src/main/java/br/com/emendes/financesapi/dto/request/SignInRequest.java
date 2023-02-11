package br.com.emendes.financesapi.dto.request;

import lombok.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SignInRequest {

  @NotBlank(message = "email must not be null or blank")
  @Email(message = "must be a well formed email")
  private String email;

  @NotBlank(message = "password must not be null or blank")
  private String password;

  // FIXME: ISSO NÃO DEVERIA ESTAR AQUI!
  public UsernamePasswordAuthenticationToken converter() {
    return new UsernamePasswordAuthenticationToken(email, password);
  }

}
