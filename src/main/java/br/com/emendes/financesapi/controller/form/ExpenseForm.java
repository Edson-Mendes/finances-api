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
import br.com.emendes.financesapi.model.Expense;
import br.com.emendes.financesapi.repository.ExpenseRepository;

public class ExpenseForm {
  
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

  public Expense convert(ExpenseRepository expenseRepository){
    exist(expenseRepository);
    LocalDate date = LocalDate.parse(this.date);
    Expense expense = new Expense(description, value, date);
    return expense;
  }

  private boolean exist(ExpenseRepository expenseRepository) {
    LocalDate date = LocalDate.parse(this.date);
    Optional<Expense> optional = expenseRepository.findByDescriptionAndMonthAndYear(description, date.getMonthValue(), date.getYear());
    if(optional.isPresent()){
      String message = "Uma despesa com essa descrição já existe em "+date.getMonth().name().toLowerCase()+" "+date.getYear();
      throw new ResponseStatusException(
			          HttpStatus.CONFLICT, message, null);
    }
    return false;
  }

  /**
   * Verifica se já existe uma despesa com a mesma descrição no mês e ano do objeto
   * e com id diferente do objeto.
   * @param incomeRepository
   * @param id
   * @return false, se não existir uma despesa com a mesma descrição em um mesmo mês e ano.
   * @throws ResponseStatusException - se existir despesa.
   */
  public boolean exist(ExpenseRepository expenseRepository, Long id) {
    LocalDate date = LocalDate.parse(this.date);
    Optional<Expense> optional = expenseRepository.findByDescriptionAndMonthAndYearAndNotId(
        description, 
        date.getMonthValue(), 
        date.getYear(),
        id);
    if(optional.isPresent()){
      String message = "Descrição de despesa duplicada para "+date.getMonth().name().toLowerCase()+" "+date.getYear();
      throw new ResponseStatusException(
			          HttpStatus.CONFLICT, message, null);
    }
    return false;
  }

}
