package br.com.emendes.financesapi.dto.request;

import br.com.emendes.financesapi.model.entity.Income;
import br.com.emendes.financesapi.model.entity.User;
import br.com.emendes.financesapi.util.Formatter;
import br.com.emendes.financesapi.validation.annotation.DateValidation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class IncomeRequest {

  @Schema(example = "Salário")
  @NotBlank(message = "description must not be null or blank")
  @Size(max = 255, message = "description must contain max {max} characters")
  private String description;

  @Schema(pattern = "yyyy-MM-dd", type = "string", example = "2023-01-04")
  @NotNull(message = "date must not be null")
  @DateValidation
  private String date;

  @Schema(example = "3240.59")
  @NotNull(message = "value must not be null")
  @Positive(message = "value must be positive")
  @Digits(integer = 6, fraction = 2,
      message = "Integer part must be max {integer} digits and fraction part must be max {fraction} digits")
  private BigDecimal value;

  // FIXME: Usar um mapper para isso.
  public Income convert(Long userId) {
    User user = new User(userId);
    return new Income(description, value, LocalDate.parse(this.date), user);
  }

  // TODO: remover! não será mais utilizado.
  public LocalDate parseDateToLocalDate() {
    return LocalDate.parse(this.date, Formatter.dateFormatter);
  }

}
