package br.com.emendes.financesapi.controller.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import br.com.emendes.financesapi.model.entity.User;
import br.com.emendes.financesapi.validation.annotation.ValidPassword;
import io.swagger.v3.oas.annotations.media.Schema;

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

  public SignupForm() {
  }

  public SignupForm(String name, String email, String password, String confirm) {
    this.name = name;
    this.email = email;
    this.password = password;
    this.confirm = confirm;
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

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getConfirm() {
    return confirm;
  }

  public void setConfirm(String confirmed) {
    this.confirm = confirmed;
  }

  public User toUser() {
    return new User(name, email, password);
  }

  public boolean passwordMatch() {
    return this.password.equals(this.confirm);
  }

}
