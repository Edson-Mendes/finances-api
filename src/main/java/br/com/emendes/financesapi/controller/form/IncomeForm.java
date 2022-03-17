package br.com.emendes.financesapi.controller.form;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import br.com.emendes.financesapi.config.validation.annotation.DateValidation;
import br.com.emendes.financesapi.model.Income;
import br.com.emendes.financesapi.repository.IncomeRepository;

public class IncomeForm {

  @NotBlank
  private String description;

  @NotNull
  @DateValidation
  private String date;

  @NotNull
  @Positive
  private BigDecimal value;

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public BigDecimal getValue() {
    return value;
  }

  public void setValue(BigDecimal value) {
    this.value = value;
  }

  public Income convert(IncomeRepository incomeRepository, Long userId) {
    alreadyExist(incomeRepository, userId);
    LocalDate date = LocalDate.parse(this.date);
    Income income = new Income(description, value, date);
    return income;
  }

  /**
   * Verifica se o usuário já possui uma receita com a mesma descrição no dado mês e ano.
   * 
   * @param incomeRepository
   * @param userId
   * @return false, se não existir uma receita com a mesma descrição em um mesmo
   *         mês e ano.
   * @throws ResponseStatusException se existir receita.
   */
  public boolean alreadyExist(IncomeRepository incomeRepository, Long userId) {
    LocalDate date = LocalDate.parse(this.date);
    Optional<Income> optional = incomeRepository.findByDescriptionAndMonthAndYearAndUserId(description, date.getMonthValue(),
        date.getYear(), userId);
    if (optional.isPresent()) {
      String message = "Uma receita com essa descrição já existe em " + date.getMonth().name().toLowerCase() + " "
          + date.getYear();
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, message, null);
    }
    return false;
  }

  /**
   * Verifica se o usuário já possui uma receita com a mesma descrição no mês e ano da
   * respectiva receita
   * e com id diferente do mesmo.
   * 
   * @param incomeRepository
   * @param id
   * @param userId
   * @return false, se não existir uma receita com a mesma descrição em um mesmo
   *         mês e ano.
   * @throws ResponseStatusException se existir receita.
   */
  public boolean alreadyExist(IncomeRepository incomeRepository, Long incomeId, Long userId) {
    LocalDate date = LocalDate.parse(this.date);
    Optional<Income> optional = incomeRepository.findByDescriptionAndMonthAndYearAndUserIdAndNotId(description,
        date.getMonthValue(), date.getYear(), userId, incomeId);
    if (optional.isPresent()) {
      String message = "Descrição de receita duplicada para " + date.getMonth().name().toLowerCase() + " "
          + date.getYear();
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, message, null);
    }
    return false;
  }

}
