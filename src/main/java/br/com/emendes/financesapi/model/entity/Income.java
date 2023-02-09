package br.com.emendes.financesapi.model.entity;

import br.com.emendes.financesapi.controller.form.IncomeForm;
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
@Table(name = "tb_income")
public class Income {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false)
  private String description;
  @Column(nullable = false, precision = 8, scale = 2)
  private BigDecimal value;
  @Column(nullable = false)
  private LocalDate date;

  @ManyToOne(fetch = FetchType.LAZY)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private User user;

  //FIXME: Substituir pelo Builder
  public Income(String description, BigDecimal value, LocalDate date, User user) {
    this.description = description;
    this.value = value;
    this.date = date;
    this.user = user;
  }

  // TODO: Criar um Mapper com um método merge.
  public void setParams(IncomeForm incomeForm) {
    this.description = incomeForm.getDescription();
    this.date = LocalDate.parse(incomeForm.getDate());
    this.value = incomeForm.getValue();
  }
}
