package br.com.emendes.financesapi.model.entity;

import br.com.emendes.financesapi.dto.request.ExpenseRequest;
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
  @Column(nullable = false)
  private String description;
  @Column(nullable = false, precision = 8, scale = 2)
  private BigDecimal value;
  @Column(nullable = false)
  private LocalDate date;

  @Column(nullable = false, length = 25)
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
  public void setParams(ExpenseRequest expenseRequest) {
    this.description = expenseRequest.getDescription();
    this.date = LocalDate.parse(expenseRequest.getDate());
    this.value = expenseRequest.getValue();
    if (expenseRequest.getCategory() != null) {
      this.category = Category.valueOf(expenseRequest.getCategory());
    }
  }

}
