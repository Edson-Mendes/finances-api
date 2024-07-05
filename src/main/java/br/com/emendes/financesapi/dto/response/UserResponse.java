package br.com.emendes.financesapi.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

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

  @Schema(example = "John Doe")
  private String name;

  @Schema(example = "john.doe@email.com")
  private String email;

}
