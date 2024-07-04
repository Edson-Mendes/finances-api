package br.com.emendes.financesapi.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
public class IncomeResponse {

  @Schema(example = "10")
  private Long id;

  @Schema(example = "Salário")
  private String description;

  @Schema(pattern = "yyyy-MM-dd", type = "string", example = "2023-01-17")
  private LocalDate date;

  @Schema(example = "3500.00")
  private BigDecimal value;

}
