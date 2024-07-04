package br.com.emendes.financesapi.unit.mapper;

import br.com.emendes.financesapi.dto.request.IncomeRequest;
import br.com.emendes.financesapi.dto.response.IncomeResponse;
import br.com.emendes.financesapi.mapper.impl.IncomeMapperImpl;
import br.com.emendes.financesapi.model.entity.Income;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static br.com.emendes.financesapi.util.faker.UserFaker.user;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Unit tests for IncomeMapperImpl.
 */
@ExtendWith(SpringExtension.class)
@DisplayName("Unit tests for IncomeMapperImpl")
class IncomeMapperImplTest {

  @InjectMocks
  private IncomeMapperImpl incomeMapper;

  @Nested
  @DisplayName("Tests for toIncome method")
  class ToIncomeMethod {

    @Test
    @DisplayName("toIncome must return Income when map successfully")
    void toIncome_MustReturnIncome_WhenMapSuccessfully() {
      IncomeRequest incomeRequest = IncomeRequest.builder()
          .description("Salário")
          .value(new BigDecimal("2500.00"))
          .date("2023-02-08")
          .build();

      Income actualIncome = incomeMapper.toIncome(incomeRequest);

      assertThat(actualIncome).isNotNull();
      assertThat(actualIncome.getDescription()).isNotNull().isEqualTo("Salário");
      assertThat(actualIncome.getValue()).isNotNull().isEqualTo("2500.00");
      assertThat(actualIncome.getDate()).isNotNull().isEqualTo("2023-02-08");
      assertThat(actualIncome.getId()).isNull();
      assertThat(actualIncome.getUser()).isNull();
    }

    @Test
    @DisplayName("toIncome must throw IllegalArgumentException when incomeRequest is null")
    void toIncome_MustThrowIllegalArgumentException_WhenIncomeRequestIsNull() {
      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> incomeMapper.toIncome(null))
          .withMessage("incomeRequest must not be null.");
    }

  }

  @Nested
  @DisplayName("Tests for toIncomeResponse method")
  class ToIncomeResponseMethod {

    @Test
    @DisplayName("toIncomeResponse must return Income when map successfully")
    void toIncomeResponse_MustReturnIncome_WhenMapSuccessfully() {
      Income income = Income.builder()
          .id(100_000L)
          .description("Salário")
          .value(new BigDecimal("2500.00"))
          .date(LocalDate.parse("2023-02-08"))
          .user(user())
          .build();

      IncomeResponse actualIncomeResponse = incomeMapper.toIncomeResponse(income);

      assertThat(actualIncomeResponse).isNotNull();
      assertThat(actualIncomeResponse.getId()).isNotNull().isEqualTo(100_000L);
      assertThat(actualIncomeResponse.getDescription()).isNotNull().isEqualTo("Salário");
      assertThat(actualIncomeResponse.getValue()).isNotNull().isEqualTo("2500.00");
      assertThat(actualIncomeResponse.getDate()).isNotNull().isEqualTo("2023-02-08");
    }

    @Test
    @DisplayName("toIncomeResponse must throw IllegalArgumentException when income is null")
    void toIncomeResponse_MustThrowIllegalArgumentException_WhenIncomeIsNull() {
      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> incomeMapper.toIncomeResponse(null))
          .withMessage("income must not be null.");
    }

  }

  @Nested
  @DisplayName("Tests for merge method")
  class MergeMethod {

    @Test
    @DisplayName("merge must update income when merge successfully")
    void merge_MustUpdateIncome_WhenMergeSuccessfully() {
      Income income = Income.builder()
          .id(100_000L)
          .description("Salário")
          .value(new BigDecimal("2500.00"))
          .date(LocalDate.parse("2023-02-08"))
          .user(user())
          .build();

      IncomeRequest incomeRequest = IncomeRequest.builder()
          .description("Salário updated")
          .value(new BigDecimal("2750.00"))
          .date("2023-02-05")
          .build();

      incomeMapper.merge(income, incomeRequest);

      assertThat(income).isNotNull();
      assertThat(income.getId()).isNotNull().isEqualTo(100_000L);
      assertThat(income.getDescription()).isNotNull().isEqualTo("Salário updated");
      assertThat(income.getValue()).isNotNull().isEqualTo("2750.00");
      assertThat(income.getDate()).isNotNull().isEqualTo("2023-02-05");
      assertThat(income.getUser()).isNotNull();
    }

    @Test
    @DisplayName("merge must throw IllegalArgumentException when income is null")
    void merge_MustThrowIllegalArgumentException_WhenIncomeIsNull() {
      IncomeRequest incomeRequest = IncomeRequest.builder()
          .description("Salário updated")
          .value(new BigDecimal("2750.00"))
          .date("2023-02-05")
          .build();

      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> incomeMapper.merge(null, incomeRequest))
          .withMessage("income must not be null.");
    }

    @Test
    @DisplayName("merge must throw IllegalArgumentException when incomeRequest is null")
    void merge_MustThrowIllegalArgumentException_WhenIncomeRequestIsNull() {
      Income income = Income.builder()
          .id(100_000L)
          .description("Salário")
          .value(new BigDecimal("2500.00"))
          .date(LocalDate.parse("2023-02-08"))
          .user(user())
          .build();

      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> incomeMapper.merge(income, null))
          .withMessage("incomeRequest must not be null.");
    }

  }

}
