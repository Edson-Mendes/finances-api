package br.com.emendes.financesapi.service.impl;

import br.com.emendes.financesapi.dto.response.SummaryResponse;
import br.com.emendes.financesapi.dto.response.ValueByCategoryResponse;
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
  public SummaryResponse monthSummary(int year, int month) {
    BigDecimal incomeTotalValue = incomeService.getTotalValueByMonthAndYearAndUserId(year, month);
    List<ValueByCategoryResponse> valuesByCategory = expenseService.getValuesByCategoryOnMonthAndYearByUser(year, month);

    if (incomeTotalValue.equals(BigDecimal.ZERO) && valuesByCategory.isEmpty()) {
      // TODO: Substituir essa Exception!
      throw new NoResultException(String.format("Has no expenses or incomes for %s %d", Month.of(month), year));
    }
    BigDecimal expenseTotalValue = valuesByCategory
        .stream().map(ValueByCategoryResponse::getValue).reduce(BigDecimal.ZERO, BigDecimal::add);

    return new SummaryResponse(incomeTotalValue, expenseTotalValue, valuesByCategory);
  }

}
