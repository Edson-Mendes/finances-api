package br.com.emendes.financesapi.controller.form;

import java.math.BigDecimal;
// TODO: Excluir essa classe form se não for necessário na aplicação.
public class UpdateIncomeForm {

  private String description;
  private String date;
  private BigDecimal value;

  public UpdateIncomeForm(String description, String date, BigDecimal value) {
    this.description = description;
    this.date = date;
    this.value = value;
  }

  public String getDescription() {
    return description;
  }

  public String getDate() {
    return date;
  }

  public BigDecimal getValue() {
    return value;
  }

}
