package br.com.emendes.financesapi.controller.dto;

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
public class UserDto {

  @Schema(example = "13")
  private Long id;

  @Schema(example = "Mei")
  private String name;

  @Schema(example = "mei@email.com")
  private String email;

  public UserDto(User user) {
    this.id = user.getId();
    this.name = user.getName();
    this.email = user.getEmail();
  }

  public static Page<UserDto> convert(Page<User> users) {
    return users.map(UserDto::new);
  }

}
