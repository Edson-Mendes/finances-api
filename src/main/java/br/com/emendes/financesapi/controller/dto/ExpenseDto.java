package br.com.emendes.financesapi.controller.dto;

import br.com.emendes.financesapi.model.Category;
import br.com.emendes.financesapi.model.entity.Expense;
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
public class ExpenseDto {

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

  public ExpenseDto(Expense expense) {
    this.id = expense.getId();
    this.description = expense.getDescription();
    this.date = expense.getDate();
    this.value = expense.getValue();
    this.category = expense.getCategory();
  }

  public static Page<ExpenseDto> convert(Page<Expense> expenses) {
    return expenses.map(ExpenseDto::new);
  }

}
