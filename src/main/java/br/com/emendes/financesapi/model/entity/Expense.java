package br.com.emendes.financesapi.model.entity;

import br.com.emendes.financesapi.controller.form.ExpenseForm;
import br.com.emendes.financesapi.model.Category;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
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

  //FIXME: Substituir pelo Builder
  public Expense(String description, BigDecimal value, LocalDate date, Category category, User user) {
    this.description = description;
    this.date = date;
    this.value = value;
    this.category = category;
    this.user = user;
  }

  // TODO: Criar um Mapper com um método merge.
  public void setParams(ExpenseForm expenseForm) {
    this.description = expenseForm.getDescription();
    this.date = LocalDate.parse(expenseForm.getDate());
    this.value = expenseForm.getValue();
    if (expenseForm.getCategory() != null) {
      this.category = expenseForm.getCategory();
    }
  }

}
