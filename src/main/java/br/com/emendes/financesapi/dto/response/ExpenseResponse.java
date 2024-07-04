package br.com.emendes.financesapi.dto.response;

import br.com.emendes.financesapi.model.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
public class ExpenseResponse {

  @Schema(example = "8")
  private Long id;

  @Schema(example = "Aluguel")
  private String description;

  @Schema(pattern = "yyyy-MM-dd", type = "string", example = "2023-01-17")
  private LocalDate date;

  @Schema(example = "1200.00")
  private BigDecimal value;

  @Schema(example = "MORADIA")
  private Category category;

}
