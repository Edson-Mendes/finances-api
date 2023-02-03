package br.com.emendes.financesapi.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;


import org.springframework.data.domain.Page;

import br.com.emendes.financesapi.model.entity.Expense;
import br.com.emendes.financesapi.model.Category;
import io.swagger.v3.oas.annotations.media.Schema;

public class ExpenseDto {

  @Schema(example = "8")
  private Long id;

  @Schema(example = "Aluguel")
  private String description;

  @JsonFormat(pattern = "dd/MM/yyyy")
  @Schema(pattern = "dd/MM/yyyy", type = "string", example = "08/01/2022")
  private LocalDate date;

  @Schema(example = "1200.00")
  private BigDecimal value;

  @Schema(example = "MORADIA")
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

  public static Page<ExpenseDto> convert(Page<Expense> expenses) {
    return expenses.map(ExpenseDto::new);
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
