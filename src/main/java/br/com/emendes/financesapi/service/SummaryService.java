package br.com.emendes.financesapi.service;

import java.math.BigDecimal;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.emendes.financesapi.controller.dto.SummaryDto;
import br.com.emendes.financesapi.model.enumerator.Category;

@Service
public class SummaryService {
  
  @Autowired
  private ExpenseService expenseService;

  @Autowired
  private IncomeService incomeService;

  public SummaryDto monthSummary(Integer year, Integer month){
    BigDecimal incomeTotalValue = incomeService.getTotalValueByMonthAndYear(year, month);
    BigDecimal expenseTotalValue = expenseService.getTotalValueByMonthAndYear(year, month);

    SummaryDto summaryDto = new SummaryDto(incomeTotalValue, expenseTotalValue, getTotalByCategory(year, month));
  
    return summaryDto;
  }

  private HashMap<Category, BigDecimal> getTotalByCategory(Integer year, Integer month){
    HashMap<Category, BigDecimal> totalByCategory = new HashMap<>();
    for(Category category : Category.values()){
      BigDecimal total = expenseService.getTotalByCategoryInYearAndMonth(category, year, month);
      totalByCategory.put(category, total);
    }
    return totalByCategory;
  }
}
