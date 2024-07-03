package br.com.emendes.financesapi.dto.request;

import br.com.emendes.financesapi.validation.annotation.NoWhiteSpace;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ChangePasswordRequest {

  @Schema(example = "myOldPassword1234")
  @NotBlank(message = "oldPassword must not be null or blank")
  private String oldPassword;
  @Schema(example = "myNewPassword1234")
  @NoWhiteSpace(message = "newPassword must not contains whitespace, tab or newline")
  @NotBlank(message = "newPassword must not be null or blank")
  @Size(min = 8, max = 30, message = "must contain between {min} and {max} characters")
  private String newPassword;

  @Schema(example = "myNewPassword1234")
  @NotBlank(message = "confirm must not be null or blank")
  private String confirm;

  @Deprecated(forRemoval = true)
  public boolean passwordMatch() {
    return this.newPassword.equals(this.confirm);
  }

}
