package br.com.emendes.financesapi.controller.form;

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
public class IncomeForm {

  @Schema(example = "Salário")
  @NotBlank
  private String description;

  @Schema(pattern = "dd/MM/yyyy", type = "string", example = "05/01/2022")
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
    LocalDate date = parseDateToLocalDate();
    User user = new User(userId);
    Income income = new Income(description, value, date, user);
    return income;
  }

  public LocalDate parseDateToLocalDate() {
    return LocalDate.parse(this.date, Formatter.dateFormatter);
  }

}
