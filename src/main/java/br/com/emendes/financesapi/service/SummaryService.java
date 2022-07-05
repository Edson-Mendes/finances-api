package br.com.emendes.financesapi.service;

import br.com.emendes.financesapi.controller.dto.SummaryDto;
import br.com.emendes.financesapi.controller.dto.ValueByCategoryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import java.math.BigDecimal;
import java.time.Month;
import java.util.List;

@Service
public class SummaryService {

  @Autowired
  private ExpenseService expenseService;
  @Autowired
  private IncomeService incomeService;

  public SummaryDto monthSummary(int year, int month) {
    BigDecimal incomeTotalValue = incomeService.getTotalValueByMonthAndYearAndUserId(year, month);
    List<ValueByCategoryDto> valuesByCategory = expenseService.getValuesByCategoryOnMonthAndYearByUser(year, month);

//    FIXME: Internal Server Error caso month seja menor que 1 e maior que 12.
    if (incomeTotalValue.equals(BigDecimal.ZERO) && valuesByCategory.isEmpty()){
      throw new NoResultException(String.format("Não há receitas e despesas para %s %d", Month.of(month), year));
    }
    BigDecimal expenseTotalValue = valuesByCategory
        .stream().map(ValueByCategoryDto::getValue).reduce(BigDecimal.ZERO, BigDecimal::add);

    return new SummaryDto(incomeTotalValue, expenseTotalValue, valuesByCategory);
  }

}
