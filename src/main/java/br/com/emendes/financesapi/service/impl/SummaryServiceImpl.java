package br.com.emendes.financesapi.service.impl;

import br.com.emendes.financesapi.controller.dto.SummaryDto;
import br.com.emendes.financesapi.controller.dto.ValueByCategoryDto;
import br.com.emendes.financesapi.service.ExpenseService;
import br.com.emendes.financesapi.service.IncomeService;
import br.com.emendes.financesapi.service.SummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import java.math.BigDecimal;
import java.time.Month;
import java.util.List;

@RequiredArgsConstructor
@Service
public class SummaryServiceImpl implements SummaryService {

  private final ExpenseService expenseService;
  private final IncomeService incomeService;

  @Override
  public SummaryDto monthSummary(int year, int month) {
    BigDecimal incomeTotalValue = incomeService.getTotalValueByMonthAndYearAndUserId(year, month);
    List<ValueByCategoryDto> valuesByCategory = expenseService.getValuesByCategoryOnMonthAndYearByUser(year, month);

    if (incomeTotalValue.equals(BigDecimal.ZERO) && valuesByCategory.isEmpty()) {
      throw new NoResultException(String.format("Não há receitas e despesas para %s %d", Month.of(month), year));
    }
    BigDecimal expenseTotalValue = valuesByCategory
        .stream().map(ValueByCategoryDto::getValue).reduce(BigDecimal.ZERO, BigDecimal::add);

    return new SummaryDto(incomeTotalValue, expenseTotalValue, valuesByCategory);
  }

}
