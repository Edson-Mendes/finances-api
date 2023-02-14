package br.com.emendes.financesapi.dto.response;

import br.com.emendes.financesapi.model.entity.Income;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.domain.Page;

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

  public IncomeResponse(Income income) {
    this.id = income.getId();
    this.description = income.getDescription();
    this.date = income.getDate();
    this.value = income.getValue();
  }

  public static Page<IncomeResponse> convert(Page<Income> incomes) {
    return incomes.map(IncomeResponse::new);
  }

}
