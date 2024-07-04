package br.com.emendes.financesapi.unit.mapper;

import br.com.emendes.financesapi.dto.request.ExpenseRequest;
import br.com.emendes.financesapi.dto.response.ExpenseResponse;
import br.com.emendes.financesapi.mapper.impl.ExpenseMapperImpl;
import br.com.emendes.financesapi.model.Category;
import br.com.emendes.financesapi.model.entity.Expense;
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
 * Unit tests for ExpenseMapperImpl.
 */
@ExtendWith(SpringExtension.class)
@DisplayName("Unit tests for ExpenseMapperImpl")
class ExpenseMapperImplTest {

  @InjectMocks
  private ExpenseMapperImpl expenseMapper;

  @Nested
  @DisplayName("Tests for toExpense method")
  class ToExpenseMethod {

    @Test
    @DisplayName("toExpense must return Expense when map successfully")
    void toExpense_MustReturnExpense_WhenMapSuccessfully() {
      ExpenseRequest expenseRequest = ExpenseRequest.builder()
          .description("Aluguel")
          .value(new BigDecimal("1500.00"))
          .date("2023-02-05")
          .category("MORADIA")
          .build();

      Expense actualExpense = expenseMapper.toExpense(expenseRequest);

      assertThat(actualExpense).isNotNull();
      assertThat(actualExpense.getDescription()).isNotNull().isEqualTo("Aluguel");
      assertThat(actualExpense.getValue()).isNotNull().isEqualTo("1500.00");
      assertThat(actualExpense.getDate()).isNotNull().isEqualTo("2023-02-05");
      assertThat(actualExpense.getCategory()).isNotNull().isEqualTo(Category.MORADIA);
      assertThat(actualExpense.getId()).isNull();
      assertThat(actualExpense.getUser()).isNull();
    }

    @Test
    @DisplayName("toExpense must return Expense with category OUTRAS when expenseRequest category field is null")
    void toExpense_MustReturnExpenseWithCategoryOUTRAS_WhenExpenseRequestCategoryFieldIsNull() {
      ExpenseRequest expenseRequest = ExpenseRequest.builder()
          .description("Aluguel")
          .value(new BigDecimal("1500.00"))
          .date("2023-02-05")
          .build();

      Expense actualExpense = expenseMapper.toExpense(expenseRequest);

      assertThat(actualExpense).isNotNull();
      assertThat(actualExpense.getDescription()).isNotNull().isEqualTo("Aluguel");
      assertThat(actualExpense.getValue()).isNotNull().isEqualTo("1500.00");
      assertThat(actualExpense.getDate()).isNotNull().isEqualTo("2023-02-05");
      assertThat(actualExpense.getCategory()).isNotNull().isEqualTo(Category.OUTRAS);
      assertThat(actualExpense.getId()).isNull();
      assertThat(actualExpense.getUser()).isNull();
    }

    @Test
    @DisplayName("toExpense must throw IllegalArgumentException when expenseRequest is null")
    void toExpense_MustThrowIllegalArgumentException_WhenExpenseRequestIsNull() {
      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> expenseMapper.toExpense(null))
          .withMessage("expenseRequest must not be null.");
    }

  }

  @Nested
  @DisplayName("Tests for toExpenseResponse method")
  class ToExpenseResponseMethod {

    @Test
    @DisplayName("toExpenseResponse must return Expense when map successfully")
    void toExpenseResponse_MustReturnExpense_WhenMapSuccessfully() {
      Expense expense = Expense.builder()
          .id(100_000L)
          .description("Aluguel")
          .value(new BigDecimal("1500.00"))
          .date(LocalDate.parse("2023-02-05"))
          .category(Category.MORADIA)
          .user(user())
          .build();

      ExpenseResponse actualExpenseResponse = expenseMapper.toExpenseResponse(expense);

      assertThat(actualExpenseResponse).isNotNull();
      assertThat(actualExpenseResponse.getId()).isNotNull().isEqualTo(100_000L);
      assertThat(actualExpenseResponse.getDescription()).isNotNull().isEqualTo("Aluguel");
      assertThat(actualExpenseResponse.getValue()).isNotNull().isEqualTo("1500.00");
      assertThat(actualExpenseResponse.getDate()).isNotNull().isEqualTo("2023-02-05");
      assertThat(actualExpenseResponse.getCategory()).isNotNull().isEqualTo(Category.MORADIA);
    }

    @Test
    @DisplayName("toExpenseResponse must throw IllegalArgumentException when expense is null")
    void toExpenseResponse_MustThrowIllegalArgumentException_WhenExpenseIsNull() {
      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> expenseMapper.toExpenseResponse(null))
          .withMessage("expense must not be null.");
    }

  }

  @Nested
  @DisplayName("Tests for merge method")
  class MergeMethod {

    @Test
    @DisplayName("merge must update expense when merge successfully")
    void merge_MustUpdateExpense_WhenMergeSuccessfully() {
      Expense expense = Expense.builder()
          .id(100_000L)
          .description("Aluguel")
          .value(new BigDecimal("1500.00"))
          .date(LocalDate.parse("2023-02-05"))
          .category(Category.MORADIA)
          .user(user())
          .build();

      ExpenseRequest expenseRequest = ExpenseRequest.builder()
          .description("Aluguel updated")
          .value(new BigDecimal("2750.00"))
          .date("2023-02-05")
          .category("MORADIA")
          .build();

      expenseMapper.merge(expense, expenseRequest);

      assertThat(expense).isNotNull();
      assertThat(expense.getId()).isNotNull().isEqualTo(100_000L);
      assertThat(expense.getDescription()).isNotNull().isEqualTo("Aluguel updated");
      assertThat(expense.getValue()).isNotNull().isEqualTo("2750.00");
      assertThat(expense.getDate()).isNotNull().isEqualTo("2023-02-05");
      assertThat(expense.getUser()).isNotNull();
    }

    @Test
    @DisplayName("merge must throw IllegalArgumentException when expense is null")
    void merge_MustThrowIllegalArgumentException_WhenExpenseIsNull() {
      ExpenseRequest expenseRequest = ExpenseRequest.builder()
          .description("Aluguel updated")
          .value(new BigDecimal("2750.00"))
          .date("2023-02-05")
          .category("MORADIA")
          .build();

      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> expenseMapper.merge(null, expenseRequest))
          .withMessage("expense must not be null.");
    }

    @Test
    @DisplayName("merge must throw IllegalArgumentException when expenseRequest is null")
    void merge_MustThrowIllegalArgumentException_WhenExpenseRequestIsNull() {
      Expense expense = Expense.builder()
          .id(100_000L)
          .description("Aluguel")
          .value(new BigDecimal("1500.00"))
          .date(LocalDate.parse("2023-02-05"))
          .category(Category.MORADIA)
          .user(user())
          .build();

      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> expenseMapper.merge(expense, null))
          .withMessage("expenseRequest must not be null.");
    }

  }

}
