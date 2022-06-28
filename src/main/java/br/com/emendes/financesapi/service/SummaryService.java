package br.com.emendes.financesapi.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.emendes.financesapi.controller.dto.SummaryDto;
import br.com.emendes.financesapi.controller.dto.ValueByCategory;
import br.com.emendes.financesapi.model.enumerator.Category;

@Service
public class SummaryService {

  @Autowired
  private ExpenseService expenseService;

  @Autowired
  private IncomeService incomeService;

  public SummaryDto monthSummary(Integer year, Integer month, Long userId) {
      Optional<BigDecimal> incomeTotalValue = incomeService.getTotalValueByMonthAndYearAndUserId(year, month, userId);
      Optional<BigDecimal> expenseTotalValue = expenseService.getTotalValueByMonthAndYearAndUser(year, month);
      if(incomeTotalValue.isPresent() || expenseTotalValue.isPresent()){
        return new SummaryDto(
            incomeTotalValue.orElse(BigDecimal.ZERO), 
            expenseTotalValue.orElse(BigDecimal.ZERO), 
            expenseTotalValue.isPresent() ? getTotalByCategory(year, month) : ValueByCategory.listWithZero());
      }
      throw new NoResultException("Não há receitas e despesas para o ano " + year + " e mês " + month);
  }

  private List<ValueByCategory> getTotalByCategory(Integer year, Integer month) {
    List<ValueByCategory> totalByCategory = new ArrayList<>();
    for (Category category : Category.values()) {
      BigDecimal total = expenseService.getTotalByCategoryOnYearAndMonth(category, year, month);
      totalByCategory.add(new ValueByCategory(category, total));
    }
    return totalByCategory;
  }
}
