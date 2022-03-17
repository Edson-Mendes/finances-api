package br.com.emendes.financesapi.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.com.emendes.financesapi.config.validation.error_dto.ErrorDto;
import br.com.emendes.financesapi.controller.dto.SummaryDto;
import br.com.emendes.financesapi.controller.dto.ValueByCategory;
import br.com.emendes.financesapi.model.enumerator.Category;

@Service
public class SummaryService {

  @Autowired
  private ExpenseService expenseService;

  @Autowired
  private IncomeService incomeService;

  public ResponseEntity<?> monthSummary(Integer year, Integer month, Long userId) {
    try {
      Optional<BigDecimal> incomeTotalValue = incomeService.getTotalValueByMonthAndYearAndUserId(year, month, userId);
      Optional<BigDecimal> expenseTotalValue = expenseService.getTotalValueByMonthAndYearAndUserId(year, month, userId);
      if(incomeTotalValue.isPresent() || expenseTotalValue.isPresent()){
        SummaryDto summaryDto = new SummaryDto(
            incomeTotalValue.orElse(BigDecimal.ZERO), 
            expenseTotalValue.orElse(BigDecimal.ZERO), 
            expenseTotalValue.isPresent() ? getTotalByCategory(year, month, userId) : ValueByCategory.listWithZero());
        return ResponseEntity.ok(summaryDto);
      }
      throw new Exception();

    } catch (Exception e) {
      String message = "Não há receitas e despesas para o ano " + year + " e mês " + month;
      ErrorDto errorDto = new ErrorDto("Not Found", message);
      return ResponseEntity
          .status(HttpStatus.NOT_FOUND)
          .header("Content-Type", "application/json;charset=UTF-8")
          .body(errorDto);
    }
  }

  private List<ValueByCategory> getTotalByCategory(Integer year, Integer month, Long userId) {
    List<ValueByCategory> totalByCategory = new ArrayList<>();
    for (Category category : Category.values()) {
      BigDecimal total = expenseService.getTotalByCategoryOnYearAndMonth(category, year, month, userId);
      totalByCategory.add(new ValueByCategory(category, total));
    }
    return totalByCategory;
  }
}
