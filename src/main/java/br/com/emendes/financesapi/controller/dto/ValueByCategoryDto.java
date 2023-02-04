package br.com.emendes.financesapi.controller.dto;

import br.com.emendes.financesapi.model.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
public class ValueByCategoryDto {

  @Schema(example = "MORADIA")
  private Category category;

  @Schema(example = "3700.00")
  private BigDecimal value;

  /**
   * @return Lista com todas as categorias com valor igual a zero.
   */
  // TODO: Entender por que isso existe
  public static List<ValueByCategoryDto> listWithZero() {
    List<ValueByCategoryDto> totalByCategory = new ArrayList<>();
    for (Category category : Category.values()) {
      totalByCategory.add(new ValueByCategoryDto(category, BigDecimal.ZERO));
    }

    return totalByCategory;
  }
}
