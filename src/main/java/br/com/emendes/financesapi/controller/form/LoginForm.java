package br.com.emendes.financesapi.controller.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import io.swagger.v3.oas.annotations.media.Schema;

public class LoginForm {

  @Schema(example = "mei@email.com")
  @NotBlank
  @Email(message = "deve ser um e-mail bem formado")
  private String email;
  
  @Schema(example = "myPassword1234")
  @NotBlank
  private String password;

  public void setEmail(String email) {
    this.email = email;
  }

  public String getEmail() {
    return email;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getPassword() {
    return password;
  }

  public UsernamePasswordAuthenticationToken converter() {
    return new UsernamePasswordAuthenticationToken(email, password);
  }

}
