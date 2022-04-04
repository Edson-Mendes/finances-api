package br.com.emendes.financesapi.controller.form;

import javax.validation.constraints.NotBlank;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import br.com.emendes.financesapi.config.validation.annotation.ValidPassword;
import io.swagger.v3.oas.annotations.media.Schema;

public class ChangePasswordForm {

  @Schema(example = "myNewPassword1234")
  @ValidPassword
  @NotBlank
  private String newPassword;

  @Schema(example = "myNewPassword1234")
  @NotBlank
  private String confirm;

  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }

  public String getNewPassword() {
    return newPassword;
  }

  public String getConfirm() {
    return confirm;
  }

  public void setConfirm(String confirm) {
    this.confirm = confirm;
  }

  public boolean passwordMatch(){
    return this.newPassword.equals(this.confirm);
  }

  public String generateNewPasswordEncoded(){
    return new BCryptPasswordEncoder().encode(newPassword);
  }
}
