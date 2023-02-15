package br.com.emendes.financesapi.dto.response;

import br.com.emendes.financesapi.model.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.domain.Page;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
public class UserResponse {

  @Schema(example = "13")
  private Long id;

  @Schema(example = "Mei")
  private String name;

  @Schema(example = "mei@email.com")
  private String email;

  public UserResponse(User user) {
    this.id = user.getId();
    this.name = user.getName();
    this.email = user.getEmail();
  }

  // Substituir isso por um mapper.
  public static Page<UserResponse> convert(Page<User> users) {
    return users.map(UserResponse::new);
  }

}
