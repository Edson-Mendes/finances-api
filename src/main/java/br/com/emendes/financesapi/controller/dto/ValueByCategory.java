package br.com.emendes.financesapi.controller.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.com.emendes.financesapi.model.enumerator.Category;
import io.swagger.v3.oas.annotations.media.Schema;

public class ValueByCategory {

  @Schema(example = "MORADIA")
  private Category category;

  @Schema(example = "3700.00")
  private BigDecimal value;

  public ValueByCategory(Category category, BigDecimal value){
    this.category = category;
    this.value = value;
  }

  public Category getCategory() {
    return category;
  }

  public BigDecimal getValue() {
    return value;
  }

  @Override
  public boolean equals(Object obj) {
    if(this == obj) return true;
    if(obj == null || getClass() != obj.getClass()) return false;
    ValueByCategory other = (ValueByCategory) obj;
    return category.equals(other.getCategory()) && value.equals(other.getValue());
  }

  @Override
  public int hashCode() {
    int result = 17;
    if(category != null){
      result = result * 31 + category.hashCode();
    }
    if(value != null){
      result = result * 31 + value.hashCode();
    }
    return result;
  }

  @Override
  public String toString() {
    return category.name()+" - "+value;
  }

  public static List<ValueByCategory> listWithZero(){
    List<ValueByCategory> totalByCategory = new ArrayList<>();
    for(Category category : Category.values()){
      totalByCategory.add(new ValueByCategory(category, BigDecimal.ZERO));
    }

    return totalByCategory;
  }
}
