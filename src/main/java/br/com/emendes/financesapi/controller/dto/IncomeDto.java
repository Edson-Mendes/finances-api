package br.com.emendes.financesapi.controller.dto;

import br.com.emendes.financesapi.model.entity.Income;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
public class IncomeDto {

  @Schema(example = "10")
  private Long id;

  @Schema(example = "Salário")
  private String description;

  @JsonFormat(pattern = "dd/MM/yyyy")
  @Schema(pattern = "dd/MM/yyyy", type = "string", example = "05/01/2022")
  private LocalDate date;

  @Schema(example = "3500.00")
  private BigDecimal value;

  public IncomeDto(Income income) {
    this.id = income.getId();
    this.description = income.getDescription();
    this.date = income.getDate();
    this.value = income.getValue();
  }

  public static Page<IncomeDto> convert(Page<Income> incomes) {
    return incomes.map(IncomeDto::new);
  }

}
