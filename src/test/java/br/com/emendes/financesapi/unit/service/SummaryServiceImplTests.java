package br.com.emendes.financesapi.unit.service;

import br.com.emendes.financesapi.controller.dto.SummaryDto;
import br.com.emendes.financesapi.controller.dto.ValueByCategoryDto;
import br.com.emendes.financesapi.model.Category;
import br.com.emendes.financesapi.service.impl.ExpenseServiceImpl;
import br.com.emendes.financesapi.service.impl.IncomeServiceImpl;
import br.com.emendes.financesapi.service.impl.SummaryServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.NoResultException;
import java.math.BigDecimal;
import java.time.Month;
import java.util.Collections;
import java.util.List;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests for SummaryService")
class SummaryServiceImplTests {

  @InjectMocks
  private SummaryServiceImpl summaryServiceImpl;
  @Mock
  private ExpenseServiceImpl expenseServiceImplMock;
  @Mock
  private IncomeServiceImpl incomeServiceImplMock;

  @BeforeEach
  public void setUp() {
    BigDecimal incomeTotalValue = new BigDecimal("2500.00");
    ValueByCategoryDto valueMoradia = new ValueByCategoryDto(Category.MORADIA, new BigDecimal("1000.00"));
    ValueByCategoryDto valueAlimentacao = new ValueByCategoryDto(Category.ALIMENTACAO, new BigDecimal("700.00"));
    ValueByCategoryDto valueTransporte = new ValueByCategoryDto(Category.TRANSPORTE, new BigDecimal("550.00"));

    List<ValueByCategoryDto> valuesByCategory = List.of(valueMoradia, valueAlimentacao, valueTransporte);

    BDDMockito.when(incomeServiceImplMock.getTotalValueByMonthAndYearAndUserId(2022, 1))
        .thenReturn(incomeTotalValue);
    BDDMockito.when(expenseServiceImplMock.getValuesByCategoryOnMonthAndYearByUser(2022, 1))
        .thenReturn(valuesByCategory);

    BDDMockito.when(incomeServiceImplMock.getTotalValueByMonthAndYearAndUserId(2022, 2))
        .thenReturn(incomeTotalValue);
    BDDMockito.when(expenseServiceImplMock.getValuesByCategoryOnMonthAndYearByUser(2022, 2))
        .thenReturn(Collections.EMPTY_LIST);

    BDDMockito.when(incomeServiceImplMock.getTotalValueByMonthAndYearAndUserId(2022, 3))
        .thenReturn(BigDecimal.ZERO);
    BDDMockito.when(expenseServiceImplMock.getValuesByCategoryOnMonthAndYearByUser(2022, 3))
        .thenReturn(valuesByCategory);

    BDDMockito.when(incomeServiceImplMock.getTotalValueByMonthAndYearAndUserId(2022, 4))
        .thenReturn(BigDecimal.ZERO);
    BDDMockito.when(expenseServiceImplMock.getValuesByCategoryOnMonthAndYearByUser(2022, 4))
        .thenReturn(Collections.EMPTY_LIST);
  }

  @Test
  @DisplayName("monthSummary must returns SummaryDto when successful")
  void monthSummary_ReturnsSummaryDto_WhenSuccessful() {
    int year = 2022;
    int month = 1;

    SummaryDto summaryDto = summaryServiceImpl.monthSummary(year, month);

    Assertions.assertThat(summaryDto).isNotNull();
    Assertions.assertThat(summaryDto.getIncomeTotalValue()).isEqualTo(new BigDecimal("2500.00"));
    Assertions.assertThat(summaryDto.getExpenseTotalValue()).isEqualTo(new BigDecimal("2250.00"));
    Assertions.assertThat(summaryDto.getFinalBalance()).isEqualTo(new BigDecimal("250.00"));
    Assertions.assertThat(summaryDto.getValuesByCategory()).hasSize(3);
  }

  @Test
  @DisplayName("monthSummary must returns SummaryDto with total expenses ZERO when successful")
  void monthSummary_ReturnsSummaryDtoWithTotalExpenseZero_WhenUserHasntExpenses() {
    int year = 2022;
    int month = 2;

    SummaryDto summaryDto = summaryServiceImpl.monthSummary(year, month);

    Assertions.assertThat(summaryDto).isNotNull();
    Assertions.assertThat(summaryDto.getIncomeTotalValue()).isEqualTo(new BigDecimal("2500.00"));
    Assertions.assertThat(summaryDto.getExpenseTotalValue()).isEqualTo(BigDecimal.ZERO);
    Assertions.assertThat(summaryDto.getFinalBalance()).isEqualTo(new BigDecimal("2500.00"));
    Assertions.assertThat(summaryDto.getValuesByCategory()).isEmpty();
  }

  @Test
  @DisplayName("monthSummary must returns SummaryDto with total incomes ZERO when successful")
  void monthSummary_ReturnsSummaryDtoWithTotalIncomeZero_WhenUserHasntIncomes() {
    int year = 2022;
    int month = 3;

    SummaryDto summaryDto = summaryServiceImpl.monthSummary(year, month);

    Assertions.assertThat(summaryDto).isNotNull();
    Assertions.assertThat(summaryDto.getIncomeTotalValue()).isEqualTo(BigDecimal.ZERO);
    Assertions.assertThat(summaryDto.getExpenseTotalValue()).isEqualTo(new BigDecimal("2250.00"));
    Assertions.assertThat(summaryDto.getFinalBalance()).isEqualTo(new BigDecimal("-2250.00"));
    Assertions.assertThat(summaryDto.getValuesByCategory()).hasSize(3);
  }

  @Test
  @DisplayName("monthSummary throws NoResultException when user don't have incomes and expenses")
  void monthSummary_ThrowsNoResultException_WhenUserDontHaveIncomesAndExpenses() {
    int year = 2022;
    int month = 4;

    Assertions.assertThatExceptionOfType(NoResultException.class)
        .isThrownBy(() -> summaryServiceImpl.monthSummary(year, month))
        .withMessage(String.format("Não há receitas e despesas para %s %d", Month.of(month), year));
  }

}
