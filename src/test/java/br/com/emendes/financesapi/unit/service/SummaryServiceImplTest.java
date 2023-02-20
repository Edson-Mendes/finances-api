package br.com.emendes.financesapi.unit.service;

import br.com.emendes.financesapi.dto.response.SummaryResponse;
import br.com.emendes.financesapi.dto.response.ValueByCategoryResponse;
import br.com.emendes.financesapi.model.Category;
import br.com.emendes.financesapi.service.impl.ExpenseServiceImpl;
import br.com.emendes.financesapi.service.impl.IncomeServiceImpl;
import br.com.emendes.financesapi.service.impl.SummaryServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.NoResultException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests for SummaryServiceImpl")
class SummaryServiceImplTest {

  @InjectMocks
  private SummaryServiceImpl summaryServiceImpl;
  @Mock
  private ExpenseServiceImpl expenseServiceImplMock;
  @Mock
  private IncomeServiceImpl incomeServiceImplMock;

  @Nested
  @DisplayName("Tests for monthSummary method")
  class MonthSummaryMethod {

    @Test
    @DisplayName("monthSummary must returns SummaryResponse when calculate month summary successfully")
    void monthSummary_MustReturnsSummaryResponse_WhenCalculateMonthSummarySuccessfully() {
      ValueByCategoryResponse valueByCategoryResponse = ValueByCategoryResponse.builder()
          .category(Category.MORADIA)
          .value(new BigDecimal("1500.00"))
          .build();

      BDDMockito.when(incomeServiceImplMock.getTotalValueByMonthAndYearAndUserId(2023, 2))
          .thenReturn(new BigDecimal("2500.00"));
      BDDMockito.when(expenseServiceImplMock.getValuesByCategoryOnMonthAndYearByUser(2023, 2))
          .thenReturn(List.of(valueByCategoryResponse));

      SummaryResponse actualSummaryResponse = summaryServiceImpl.monthSummary(2023, 2);

      Assertions.assertThat(actualSummaryResponse).isNotNull();
      Assertions.assertThat(actualSummaryResponse.getIncomeTotalValue()).isEqualTo(new BigDecimal("2500.00"));
      Assertions.assertThat(actualSummaryResponse.getExpenseTotalValue()).isEqualTo(new BigDecimal("1500.00"));
      Assertions.assertThat(actualSummaryResponse.getFinalBalance()).isEqualTo(new BigDecimal("1000.00"));
      Assertions.assertThat(actualSummaryResponse.getValuesByCategory()).hasSize(1);
    }

    @Test
    @DisplayName("monthSummary must returns SummaryResponse with total expenses ZERO when user has no expenses")
    void monthSummary_ReturnsSummaryResponseWithTotalExpenseZero_WhenUserHasNoExpenses() {
      BDDMockito.when(incomeServiceImplMock.getTotalValueByMonthAndYearAndUserId(2023, 2))
          .thenReturn(new BigDecimal("2500.00"));
      BDDMockito.when(expenseServiceImplMock.getValuesByCategoryOnMonthAndYearByUser(2023, 2))
          .thenReturn(Collections.emptyList());

      SummaryResponse actualSummaryResponse = summaryServiceImpl.monthSummary(2023, 2);

      Assertions.assertThat(actualSummaryResponse).isNotNull();
      Assertions.assertThat(actualSummaryResponse.getIncomeTotalValue()).isEqualTo(new BigDecimal("2500.00"));
      Assertions.assertThat(actualSummaryResponse.getExpenseTotalValue()).isEqualTo(BigDecimal.ZERO);
      Assertions.assertThat(actualSummaryResponse.getFinalBalance()).isEqualTo(new BigDecimal("2500.00"));
      Assertions.assertThat(actualSummaryResponse.getValuesByCategory()).isEmpty();
    }

    @Test
    @DisplayName("monthSummary must returns SummaryResponse with total incomes ZERO when successful")
    void monthSummary_ReturnsSummaryResponseWithTotalIncomeZero_WhenUserHasNoIncomes() {
      ValueByCategoryResponse valueByCategoryResponse = ValueByCategoryResponse.builder()
          .category(Category.MORADIA)
          .value(new BigDecimal("1500.00"))
          .build();

      BDDMockito.when(incomeServiceImplMock.getTotalValueByMonthAndYearAndUserId(2023, 2))
          .thenReturn(BigDecimal.ZERO);
      BDDMockito.when(expenseServiceImplMock.getValuesByCategoryOnMonthAndYearByUser(2023, 2))
          .thenReturn(List.of(valueByCategoryResponse));

      SummaryResponse summaryResponse = summaryServiceImpl.monthSummary(2023, 2);

      Assertions.assertThat(summaryResponse).isNotNull();
      Assertions.assertThat(summaryResponse.getIncomeTotalValue()).isEqualTo(BigDecimal.ZERO);
      Assertions.assertThat(summaryResponse.getExpenseTotalValue()).isEqualTo(new BigDecimal("1500.00"));
      Assertions.assertThat(summaryResponse.getFinalBalance()).isEqualTo(new BigDecimal("-1500.00"));
      Assertions.assertThat(summaryResponse.getValuesByCategory()).hasSize(1);
    }

    @Test
    @DisplayName("monthSummary throws NoResultException when user has no incomes and expenses")
    void monthSummary_ThrowsNoResultException_WhenUserHasNoIncomesAndExpenses() {
      BDDMockito.when(incomeServiceImplMock.getTotalValueByMonthAndYearAndUserId(2023, 3))
          .thenReturn(BigDecimal.ZERO);
      BDDMockito.when(expenseServiceImplMock.getValuesByCategoryOnMonthAndYearByUser(2023, 3))
          .thenReturn(Collections.emptyList());

      Assertions.assertThatExceptionOfType(NoResultException.class)
          .isThrownBy(() -> summaryServiceImpl.monthSummary(2023, 3))
          .withMessage("Has no expenses or incomes for MARCH 2023");
    }

  }

}
