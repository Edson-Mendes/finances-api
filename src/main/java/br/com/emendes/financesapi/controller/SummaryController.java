package br.com.emendes.financesapi.controller;

import java.math.BigDecimal;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.emendes.financesapi.controller.dto.SummaryDto;
import br.com.emendes.financesapi.model.enumerator.Category;
import br.com.emendes.financesapi.repository.ExpenseRepository;
import br.com.emendes.financesapi.repository.IncomeRepository;
// TODO: Dividir o código em partes.
@RestController
@RequestMapping("/resumo")
public class SummaryController {

  @Autowired
  private ExpenseRepository expenseRepository;

  @Autowired
  private IncomeRepository incomeRepository;

  @GetMapping("/{year}/{month}")
  public SummaryDto monthSummary(@PathVariable Integer year, @PathVariable Integer month){
    
    BigDecimal incomeTotalValue = incomeRepository.getTotalValueByMonthAndYear(year, month);
    BigDecimal expenseTotalValue = expenseRepository.getTotalValueByMonthAndYear(year, month);
    
    if(incomeTotalValue == null){
      incomeTotalValue = BigDecimal.ZERO;
    }
    if(expenseTotalValue == null){
      expenseTotalValue = BigDecimal.ZERO;
    }
    
    SummaryDto summaryDto = new SummaryDto(incomeTotalValue, expenseTotalValue, getTotalByCategory(year, month));
  
    return summaryDto;
  }

  private HashMap<Category, BigDecimal> getTotalByCategory(Integer year, Integer month){
    HashMap<Category, BigDecimal> totalByCategory = new HashMap<>();
    for(Category category : Category.values()){
      BigDecimal total = expenseRepository.getTotalByCategoryInYearAndMonth(category, year, month);
      if(total == null){
        total = BigDecimal.ZERO;
      }
      totalByCategory.put(category, total);
    }
    return totalByCategory;
  }

}
