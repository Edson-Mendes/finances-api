package br.com.emendes.financesapi.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

 public static List<IncomeDto> convert(List<Income> incomes){
   List<IncomeDto> incomesDto = new ArrayList<>();

  incomes.forEach(income -> incomesDto.add(new IncomeDto(income)));

   return incomesDto;
 }
}
