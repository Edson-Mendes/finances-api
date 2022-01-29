package br.com.emendes.financesapi.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import br.com.emendes.financesapi.controller.form.ExpenseForm;
import br.com.emendes.financesapi.model.enumerator.Category;

@Entity
public class Expense {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(columnDefinition = "VARCHAR_IGNORECASE(255)")
  private String description;
  private BigDecimal value;
  private LocalDate date;

  // TODO: Criar uma tabela de categorias ou apenas enumera-las na aplicação?
  @Enumerated(EnumType.STRING)
  private Category category;

  public Expense() {
  }

  public Expense(String description, BigDecimal value, LocalDate date, Category category) {
    this.description = description;
    this.date = date;
    this.value = value;
    this.category = category;
  }

  public void setParams(ExpenseForm expenseForm) {
    this.description = expenseForm.getDescription();
    this.date = LocalDate.parse(expenseForm.getDate());
    this.value = expenseForm.getValue();
    if (expenseForm.getCategory() != null) {
      this.category = expenseForm.getCategory();
    }
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

  public BigDecimal getValue() {
    return value;
  }

  public void setValue(BigDecimal value) {
    this.value = value;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }
}
