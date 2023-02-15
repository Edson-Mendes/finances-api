package br.com.emendes.financesapi.controller.form;

import br.com.emendes.financesapi.validation.annotation.ValidPassword;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ChangePasswordRequest {

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

  public boolean passwordMatch() {
    return this.newPassword.equals(this.confirm);
  }

  // TODO: remover daqui o encode do password!
  public String generateNewPasswordEncoded() {
    return new BCryptPasswordEncoder().encode(newPassword);
  }
}
