package br.com.emendes.financesapi.controller.form;

import javax.validation.constraints.NotBlank;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import br.com.emendes.financesapi.validation.annotation.ValidPassword;
import io.swagger.v3.oas.annotations.media.Schema;

public class ChangePasswordForm {

  @Schema(example = "myOldPassword1234")
  @NotBlank
  private String oldPassword;
  @Schema(example = "myNewPassword1234")
  @ValidPassword
  @NotBlank
  private String newPassword;

  @Schema(example = "myNewPassword1234")
  @NotBlank
  private String confirm;

  public ChangePasswordForm() {
  }

  public ChangePasswordForm(String oldPassword, String newPassword, String confirm) {
    this.oldPassword = oldPassword;
    this.newPassword = newPassword;
    this.confirm = confirm;
  }

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

  public String getOldPassword() { return oldPassword; }

  public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }

  public boolean passwordMatch() {
    return this.newPassword.equals(this.confirm);
  }

  public String generateNewPasswordEncoded() {
    return new BCryptPasswordEncoder().encode(newPassword);
  }
}
