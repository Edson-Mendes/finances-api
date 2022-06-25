package br.com.emendes.financesapi.unit.service;

import java.math.BigDecimal;
import java.util.Optional;

import javax.persistence.NoResultException;

import br.com.emendes.financesapi.service.ExpenseService;
import br.com.emendes.financesapi.service.IncomeService;
import br.com.emendes.financesapi.service.SummaryService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.emendes.financesapi.controller.dto.SummaryDto;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests for SummaryService")
public class SummaryServiceTests {

  @InjectMocks
  private SummaryService summaryService;

  @Mock
  private ExpenseService expenseServiceMock;
  @Mock
  IncomeService incomeServiceMock;

  @BeforeEach
  public void setUp() {
    BigDecimal incomeTotalValue = new BigDecimal("2500.00");

    BDDMockito.when(incomeServiceMock.getTotalValueByMonthAndYearAndUserId(2022, 1, 100l))
        .thenReturn(Optional.of(incomeTotalValue));

    BDDMockito.when(expenseServiceMock.getTotalValueByMonthAndYearAndUserId(2022, 1, 100l))
        .thenReturn(Optional.of(BigDecimal.ZERO));

    BDDMockito.when(expenseServiceMock.getTotalByCategoryOnYearAndMonth(
        ArgumentMatchers.any(),
        ArgumentMatchers.eq(2022),
        ArgumentMatchers.eq(1),
        ArgumentMatchers.eq(100l)))
        .thenReturn(BigDecimal.ZERO);

    BDDMockito.when(incomeServiceMock.getTotalValueByMonthAndYearAndUserId(2022, 2, 100l))
        .thenReturn(Optional.empty());

    BDDMockito.when(expenseServiceMock.getTotalValueByMonthAndYearAndUserId(2022, 2, 100l))
        .thenReturn(Optional.empty());
  }

  @Test
  @DisplayName("monthSummary must returns SummaryDto when successful")
  void monthSummary_ReturnsSummaryDto_WhenSuccessful() {
    Integer year = 2022;
    Integer month = 1;
    Long userId = 100l;

    SummaryDto summaryDto = summaryService.monthSummary(year, month, userId);

    Assertions.assertThat(summaryDto).isNotNull();
    Assertions.assertThat(summaryDto.getIncomeTotalValue()).isEqualTo(new BigDecimal("2500.00"));
    Assertions.assertThat(summaryDto.getExpenseTotalValue()).isEqualTo(BigDecimal.ZERO);
  }

  @Test
  @DisplayName("monthSummary throws NoResultException when user don't have incomes and expenses")
  void monthSummary_ThrowsNoResultException_WhenUserDontHaveIncomesAndExpenses() {
    Integer year = 2022;
    Integer month = 2;
    Long userId = 100l;

    Assertions.assertThatExceptionOfType(NoResultException.class)
        .isThrownBy(() -> summaryService.monthSummary(year, month, userId))
        .withMessageContaining("Não há receitas e despesas para o ano ");
  }

}
