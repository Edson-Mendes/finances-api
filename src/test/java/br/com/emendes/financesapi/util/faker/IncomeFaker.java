package br.com.emendes.financesapi.util.faker;

import br.com.emendes.financesapi.model.entity.Income;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static br.com.emendes.financesapi.util.faker.UserFaker.user;

/**
 * Classe com objetos relacionados a {@link Income} para serem usados em testes automatizados.
 */
public class IncomeFaker {

  public static final Long INCOME_ID = 100_000L;
  public static final String INCOME_DESCRIPTION = "Salário";
  public static final BigDecimal INCOME_VALUE = new BigDecimal("2500.00");
  public static final LocalDate INCOME_DATE = LocalDate.parse("2023-02-08");

  /**
   * Retorna uma {@link Income} com todos os campos.
   */
  public static Income income() {
    return Income.builder()
        .id(INCOME_ID)
        .description(INCOME_DESCRIPTION)
        .value(INCOME_VALUE)
        .date(INCOME_DATE)
        .user(user())
        .build();
  }

  /**
   * Retorna um {@code List<Income>} com um elemento.
   */
  public static List<Income> incomeList() {
    return List.of(income());
  }

  /**
   * Retorna um {@code Optional<Income>} não vazio.
   */
  public static Optional<Income> incomeOptional() {
    return Optional.of(income());
  }

}
