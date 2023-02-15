package br.com.emendes.financesapi.util.creator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.com.emendes.financesapi.dto.response.SummaryResponse;
import br.com.emendes.financesapi.dto.response.ValueByCategoryResponse;
import br.com.emendes.financesapi.model.Category;

public class SummaryDtoCreator {

  public static SummaryResponse simpleSummaryDto() {
    BigDecimal incomeTotalValue = new BigDecimal("2500.00");
    BigDecimal expenseTotalValue = new BigDecimal("2000.00");
    List<ValueByCategoryResponse> valuesByCategory = new ArrayList<>();

    for (Category category : Category.values()) {
      valuesByCategory.add(new ValueByCategoryResponse(category, new BigDecimal("250.00")));
    }

    return new SummaryResponse(incomeTotalValue, expenseTotalValue, valuesByCategory);
  }

}
