package br.com.emendes.financesapi.dto.request;

import br.com.emendes.financesapi.model.entity.Income;
import br.com.emendes.financesapi.model.entity.User;
import br.com.emendes.financesapi.util.Formatter;
import br.com.emendes.financesapi.validation.annotation.DateValidation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class IncomeRequest {

  @Schema(example = "Salário")
  @NotBlank
  private String description;

  @Schema(pattern = "yyyy-MM-dd", type = "string", example = "2023-01-04")
  @NotNull
  @DateValidation
  private String date;

  @Schema(example = "3240.59")
  @NotNull
  @Positive
  @Digits(integer = 6, fraction = 2)
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
