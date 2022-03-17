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

  public ExpenseDto() {
  }

  public ExpenseDto(Expense expense) {
    this.id = expense.getId();
    this.description = expense.getDescription();
    this.date = expense.getDate();
    this.value = expense.getValue();
    this.category = expense.getCategory();
  }

  public ExpenseDto(Long id, String description, LocalDate date, BigDecimal value, Category category) {
    this.id = id;
    this.description = description;
    this.date = date;
    this.value = value;
    this.category = category;
  }

  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public BigDecimal getValue() {
    return value;
  }

  public void setValue(BigDecimal value) {
    this.value = value;
  }

  public static List<ExpenseDto> convert(List<Expense> expenses) {
    List<ExpenseDto> expensesDto = new ArrayList<>();

    expenses.forEach(expense -> expensesDto.add(new ExpenseDto(expense)));

    return expensesDto;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || obj.getClass() != getClass()) {
      return false;
    }

    ExpenseDto other = (ExpenseDto) obj;

    return this.description.equals(other.getDescription())
        && this.date.equals(other.getDate())
        && this.value.equals(other.getValue())
        && this.category.equals(other.getCategory());
  }

  @Override
  public int hashCode() {
    int result = 17;

    if (description != null) {
      result = result * 31 + description.hashCode();
    }
    if (date != null) {
      result = result * 31 + date.hashCode();
    }
    if (value != null) {
      result = result * 31 + value.hashCode();
    }
    if(category != null){
      result = result * 31 + category.hashCode();
    }

    return result;
  }

  @Override
  public String toString() {
    return "ExpenseDto:{id:" + id + 
        ", description:" + description + 
        ", date: " + date + 
        ", value: " + value + 
        ", category: " + category.name() +
        "}";
  }
}
