package br.com.emendes.financesapi.dto.response;

import br.com.emendes.financesapi.model.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
public class ValueByCategoryResponse {

  @Schema(example = "MORADIA")
  private Category category;

  @Schema(example = "3700.00")
  private BigDecimal value;

}
