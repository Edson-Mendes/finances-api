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

  @Schema(pattern = "yyyy-MM-dd", type = "string", example = "2023-04-15")
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

  // FIXME: Em vez de ter um converter aqui, adicionar alguma biblioteca mapper.
  public Expense convert(Long userId) {
    if (category == null) {
      category = Category.OUTRAS;
    }
    User user = new User(userId);
    return new Expense(description, value, LocalDate.parse(this.date), category, user);
  }

  // TODO: remover! não será mais utilizado.
  public LocalDate parseDateToLocalDate() {
    return LocalDate.parse(this.date, Formatter.dateFormatter);
  }

}
