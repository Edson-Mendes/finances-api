package br.com.emendes.financesapi.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.com.emendes.financesapi.model.Income;

public class IncomeDto {
  private Long id;
  private String description;
  private LocalDate date;
  private BigDecimal value;

  public IncomeDto(Income income){
    this.id = income.getId();
    this.description = income.getDescription();
    this.date = income.getDate();
    this.value = income.getValue();
  }

 public Long getId(){
   return id;
 }

 public String getDescription(){
   return description;
 }

 public LocalDate getDate(){
   return date;
 }

 public BigDecimal getValue(){
   return value;
 }
}
