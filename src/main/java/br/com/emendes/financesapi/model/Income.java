package br.com.emendes.financesapi.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.*;

import br.com.emendes.financesapi.controller.form.IncomeForm;
import br.com.emendes.financesapi.util.Formatter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "tb_income")
public class Income {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String description;
  private BigDecimal value;
  private LocalDate date;

  @ManyToOne
  @OnDelete(action = OnDeleteAction.CASCADE)
  private User user;

  public Income() {
  }

  public Income(String description, BigDecimal value, LocalDate date, User user) {
    this.description = description;
    this.value = value;
    this.date = date;
    this.user = user;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
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

  public void setParams(IncomeForm incomeForm) {
    this.description = incomeForm.getDescription();
    this.date = LocalDate.parse(incomeForm.getDate(), Formatter.dateFormatter);
    this.value = incomeForm.getValue();
  }
}
