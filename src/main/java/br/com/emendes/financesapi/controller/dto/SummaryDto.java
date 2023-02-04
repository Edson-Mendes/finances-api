package br.com.emendes.financesapi.controller.dto;

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
public class SummaryDto {

  @Schema(example = "5000.00")
  private BigDecimal incomeTotalValue = BigDecimal.ZERO;

  @Schema(example = "3700.00")
  private BigDecimal expenseTotalValue = BigDecimal.ZERO;

  @Schema(example = "1300.00")
  private BigDecimal finalBalance = BigDecimal.ZERO;

  private List<ValueByCategoryDto> valuesByCategory = new ArrayList<>();

  public SummaryDto(BigDecimal incomeTotalValue, BigDecimal expenseTotalValue,
                    List<ValueByCategoryDto> valuesByCategory) {
    this.incomeTotalValue = incomeTotalValue;
    this.expenseTotalValue = expenseTotalValue;
    this.finalBalance = incomeTotalValue.subtract(expenseTotalValue);
    this.valuesByCategory = valuesByCategory;
  }

}