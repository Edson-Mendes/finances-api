package br.com.emendes.financesapi.controller.form;

import br.com.emendes.financesapi.model.Category;
import br.com.emendes.financesapi.model.entity.Expense;
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
public class ExpenseForm {

  @Schema(example = "Mercado")
  @NotBlank
  private String description;

  @Schema(pattern = "dd/MM/yyyy", type = "string", example = "17/01/2022")
  @NotNull
  @DateValidation
  private String date;

  @Schema(example = "271.94")
  @NotNull
  @Positive
  @Digits(integer = 6, fraction = 2)
  private BigDecimal value;

  //  TODO: Fazer um bean validation para validar a categoria
  @Schema(example = "ALIMENTACAO")
  private Category category;

  public Expense convert(Long userId) {
    LocalDate date = parseDateToLocalDate();
    if (category == null) {
      category = Category.OUTRAS;
    }
    User user = new User(userId);
    Expense expense = new Expense(description, value, date, category, user);
    return expense;
  }

  public LocalDate parseDateToLocalDate() {
    return LocalDate.parse(this.date, Formatter.dateFormatter);
  }

}
