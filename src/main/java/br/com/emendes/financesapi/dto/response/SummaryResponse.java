package br.com.emendes.financesapi.dto.response;

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
public class SummaryResponse {

  @Schema(example = "5000.00")
  private BigDecimal incomeTotalValue = BigDecimal.ZERO;

  @Schema(example = "3700.00")
  private BigDecimal expenseTotalValue = BigDecimal.ZERO;

  @Schema(example = "1300.00")
  private BigDecimal finalBalance = BigDecimal.ZERO;

  private List<ValueByCategoryResponse> valuesByCategory = new ArrayList<>();

  public SummaryResponse(BigDecimal incomeTotalValue, BigDecimal expenseTotalValue,
                         List<ValueByCategoryResponse> valuesByCategory) {
    this.incomeTotalValue = incomeTotalValue;
    this.expenseTotalValue = expenseTotalValue;
    this.finalBalance = incomeTotalValue.subtract(expenseTotalValue);
    this.valuesByCategory = valuesByCategory;
  }

}