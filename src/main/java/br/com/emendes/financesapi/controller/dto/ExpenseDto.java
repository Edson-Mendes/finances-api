package br.com.emendes.financesapi.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.com.emendes.financesapi.model.Expense;
import br.com.emendes.financesapi.model.enumerator.Category;

public class ExpenseDto {

  private Long id;
  private String description;
  private LocalDate date;
  private BigDecimal value;
  private Category category;

  public ExpenseDto(Expense expense) {
    this.id = expense.getId();
    this.description = expense.getDescription();
    this.date = expense.getDate();
    this.value = expense.getValue();
    this.category = expense.getCategory();
  }

  public Category getCategory() {
    return category;
  }

  public Long getId() {
    return id;
  }

  public String getDescription() {
    return description;
  }

  public LocalDate getDate() {
    return date;
  }

  public BigDecimal getValue() {
    return value;
  }

  public static List<ExpenseDto> convert(List<Expense> expenses) {
    List<ExpenseDto> expensesDto = new ArrayList<>();

    expenses.forEach(expense -> expensesDto.add(new ExpenseDto(expense)));

    return expensesDto;
  }
}
