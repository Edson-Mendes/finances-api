package br.com.emendes.financesapi.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.*;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import br.com.emendes.financesapi.controller.form.ExpenseForm;
import br.com.emendes.financesapi.model.enumerator.Category;
import br.com.emendes.financesapi.util.Formatter;

@Entity
@Table(name = "tb_expense")
public class Expense {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String description;
  private BigDecimal value;
  private LocalDate date;

  @Enumerated(EnumType.STRING)
  private Category category;

  @ManyToOne(fetch = FetchType.LAZY)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private User user;

  public Expense() {
  }

  public Expense(String description, BigDecimal value, LocalDate date, Category category, User user) {
    this.description = description;
    this.date = date;
    this.value = value;
    this.category = category;
    this.user = user;
  }

  public void setParams(ExpenseForm expenseForm) {
    this.description = expenseForm.getDescription();
    this.date = LocalDate.parse(expenseForm.getDate(), Formatter.dateFormatter);
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

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}
