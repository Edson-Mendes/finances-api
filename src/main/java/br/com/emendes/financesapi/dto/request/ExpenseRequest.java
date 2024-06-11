package br.com.emendes.financesapi.dto.request;

import br.com.emendes.financesapi.model.Category;
import br.com.emendes.financesapi.model.entity.Expense;
import br.com.emendes.financesapi.model.entity.User;
import br.com.emendes.financesapi.validation.annotation.CategoryValidation;
import br.com.emendes.financesapi.validation.annotation.DateValidation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ExpenseRequest {

  @Schema(example = "Mercado")
  @NotBlank(message = "description must not be null or blank")
  @Size(max = 255, message = "description must contain max {max} characters")
  private String description;

  @Schema(pattern = "yyyy-MM-dd", type = "string", example = "2023-04-15")
  @NotNull(message = "date must not be null")
  @DateValidation
  private String date;

  @Schema(example = "271.94")
  @NotNull(message = "value must not be null")
  @Positive(message = "value must be positive")
  @Digits(integer = 6, fraction = 2,
      message = "Integer part must be max {integer} digits and fraction part must be max {fraction} digits")
  private BigDecimal value;

  @Schema(example = "ALIMENTACAO")
  @NotBlank(message = "category must not be null or blank")
  @CategoryValidation
  private String category;

  // FIXME: Em vez de ter um converter aqui, adicionar alguma biblioteca mapper.
  public Expense convert(Long userId) {
    if (category == null) {
      category = Category.OUTRAS.name();
    }
    User user = new User(userId);
    return new Expense(description, value, LocalDate.parse(this.date), Category.valueOf(category), user);
  }

}
