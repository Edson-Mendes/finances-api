package br.com.emendes.financesapi.creator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.com.emendes.financesapi.controller.dto.SummaryDto;
import br.com.emendes.financesapi.controller.dto.ValueByCategory;
import br.com.emendes.financesapi.model.enumerator.Category;

public class SummaryDtoCreator {

  public static SummaryDto simpleSummaryDto() {
    BigDecimal incomeTotalValue = new BigDecimal("2500.00");
    BigDecimal expenseTotalValue = new BigDecimal("2000.00");
    List<ValueByCategory> valuesByCategory = new ArrayList<>();

    for (Category category : Category.values()) {
      valuesByCategory.add(new ValueByCategory(category, new BigDecimal("250.00")));
    }

    return new SummaryDto(incomeTotalValue, expenseTotalValue, valuesByCategory);
  }

}