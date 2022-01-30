package br.com.emendes.financesapi.controller.dto;

import java.math.BigDecimal;
import java.util.HashMap;

import br.com.emendes.financesapi.model.enumerator.Category;

public class SummaryDto {
  private BigDecimal incomeTotalValue = BigDecimal.ZERO;
  private BigDecimal expenseTotalValue = BigDecimal.ZERO;
  private BigDecimal finalBalance = BigDecimal.ZERO;
  private HashMap<Category, BigDecimal> totalByCategory = new HashMap<>();

  public SummaryDto() {}

  public SummaryDto(BigDecimal incomeTotalValue, BigDecimal expenseTotalValue, HashMap<Category, BigDecimal> totalByCategory) {
    this.incomeTotalValue = incomeTotalValue;
    this.expenseTotalValue = expenseTotalValue;
    this.finalBalance = incomeTotalValue.subtract(expenseTotalValue);
    this.totalByCategory = totalByCategory;
  }

  public BigDecimal getIncomeTotalValue() {
    return incomeTotalValue;
  }

  public BigDecimal getFinalBalance() {
    return finalBalance;
  }

  public BigDecimal getExpenseTotalValue() {
    return expenseTotalValue;
  }

  public HashMap<Category, BigDecimal> getValuesByCategory(){
    return totalByCategory;
  }

}