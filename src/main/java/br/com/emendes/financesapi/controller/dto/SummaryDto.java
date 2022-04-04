package br.com.emendes.financesapi.controller.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public class SummaryDto {

  @Schema(example = "5000.00")
  private BigDecimal incomeTotalValue = BigDecimal.ZERO;

  @Schema(example = "3700.00")
  private BigDecimal expenseTotalValue = BigDecimal.ZERO;

  @Schema(example = "1300.00")
  private BigDecimal finalBalance = BigDecimal.ZERO;
  
  private List<ValueByCategory> valuesByCategory = new ArrayList<>();

  public SummaryDto() {
  }

  public SummaryDto(BigDecimal incomeTotalValue, BigDecimal expenseTotalValue,
      List<ValueByCategory> valuesByCategory) {
    this.incomeTotalValue = incomeTotalValue;
    this.expenseTotalValue = expenseTotalValue;
    this.finalBalance = incomeTotalValue.subtract(expenseTotalValue);
    this.valuesByCategory = valuesByCategory;
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

  public List<ValueByCategory> getValuesByCategory() {
    return valuesByCategory;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null || getClass() != obj.getClass())
      return false;
    SummaryDto other = (SummaryDto) obj;
    boolean listValuesByCategoryIsEqual = false;
    for (int i = 0; i < valuesByCategory.size(); i++) {
      listValuesByCategoryIsEqual = other.getValuesByCategory().contains(valuesByCategory.get(i));
    }
    return listValuesByCategoryIsEqual && 
        incomeTotalValue.equals(other.getIncomeTotalValue()) &&
        expenseTotalValue.equals(other.getExpenseTotalValue()) &&
        finalBalance.equals(other.getFinalBalance());
  }

  @Override
  public int hashCode() {
    int result = 17;
    if (incomeTotalValue != null) {
      result = result * 31 + incomeTotalValue.hashCode();
    }
    if (expenseTotalValue != null) {
      result = result * 31 + expenseTotalValue.hashCode();
    }
    if (finalBalance != null) {
      result = result * 31 + finalBalance.hashCode();
    }
    for(int i = 0; i< valuesByCategory.size(); i++){
      result = result * 31 + valuesByCategory.get(i).hashCode();
    }
    return result;
  }

  @Override
  public String toString() {
    return "SummaryDto{incomeTotalValue=" +
        incomeTotalValue + ", expenseTotalValue=" +
        expenseTotalValue + ", finalBalance=" +
        finalBalance + ", valuesByCategory=["+
        valuesByCategory.toString()+"]"+"}";
  }

}