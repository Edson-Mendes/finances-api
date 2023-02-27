package br.com.emendes.financesapi.unit.service;

import br.com.emendes.financesapi.dto.request.ExpenseRequest;
import br.com.emendes.financesapi.dto.response.ExpenseResponse;
import br.com.emendes.financesapi.dto.response.ValueByCategoryResponse;
import br.com.emendes.financesapi.exception.EntityNotFoundException;
import br.com.emendes.financesapi.model.Category;
import br.com.emendes.financesapi.model.entity.Expense;
import br.com.emendes.financesapi.repository.ExpenseRepository;
import br.com.emendes.financesapi.service.impl.ExpenseServiceImpl;
import br.com.emendes.financesapi.util.AuthenticationFacade;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static br.com.emendes.financesapi.util.constant.ConstantForTesting.EXPENSE;
import static br.com.emendes.financesapi.util.constant.ConstantForTesting.USER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(SpringExtension.class)
@DisplayName("Unit tests for ExpenseServiceImpl")
class ExpenseServiceImplTest {

  @InjectMocks
  private ExpenseServiceImpl expenseServiceImpl;

  @Mock
  private ExpenseRepository expenseRepositoryMock;
  @Mock
  private AuthenticationFacade authenticationFacadeMock;

  private final Pageable PAGEABLE = PageRequest.of(0, 10, Direction.DESC, "date");
  private final Pageable PAGEABLE_WITH_PAGE_ONE = PageRequest.of(1, 10, Direction.DESC, "date");

  @Nested
  @DisplayName("Tests for create method")
  class CreateMethod {

    @BeforeEach
    void setUp() {
      BDDMockito.when(authenticationFacadeMock.getAuthentication())
          .thenReturn(new TestingAuthenticationToken(USER, null));
    }

    @Test
    @DisplayName("create must returns ExpenseResponse when create successfully")
    void create_MustReturnsExpenseResponse_WhenCreateSuccessfully() {
      BDDMockito.when(expenseRepositoryMock.save(any(Expense.class)))
          .thenReturn(EXPENSE);

      ExpenseRequest expenseRequest = ExpenseRequest.builder()
          .description("Aluguel xpto")
          .value(new BigDecimal("1500.00"))
          .date("2023-02-05")
          .category("MORADIA")
          .build();

      ExpenseResponse actualExpenseResponse = expenseServiceImpl.create(expenseRequest);

      Assertions.assertThat(actualExpenseResponse).isNotNull();
      Assertions.assertThat(actualExpenseResponse.getDescription()).isEqualTo("Aluguel xpto");
      Assertions.assertThat(actualExpenseResponse.getValue()).isEqualTo(new BigDecimal("1500.00"));
      Assertions.assertThat(actualExpenseResponse.getDate()).isEqualTo("2023-02-05");
    }

  }

  @Nested
  @DisplayName("Tests for readAllByUser method")
  class ReadAllByUserMethod {

    @Test
    @DisplayName("readAllByUser must returns Page<ExpenseResponse> when read all by user successfully")
    void readAllByUser_MustReturnsPageExpenseResponse_WhenReadAllByUserSuccessfully() {
      BDDMockito.when(expenseRepositoryMock.findAllByUser(any()))
          .thenReturn(new PageImpl<>(List.of(EXPENSE)));

      Page<ExpenseResponse> actualExpenseResponsePage = expenseServiceImpl.readAllByUser(PAGEABLE);
      List<ExpenseResponse> actualContent = actualExpenseResponsePage.getContent();

      Assertions.assertThat(actualExpenseResponsePage).isNotEmpty();
      Assertions.assertThat(actualExpenseResponsePage.getNumberOfElements()).isEqualTo(1);
      Assertions.assertThat(actualContent.get(0).getDescription()).isEqualTo("Aluguel xpto");
      Assertions.assertThat(actualContent.get(0).getValue()).isEqualTo("1500.00");
    }

    @Test
    @DisplayName("readAllByUser must throws EntityNotFoundException when user has no expenses")
    void readAllByUser_ThrowsEntityNotFoundException_WhenUserHasNoExpenses() {
      BDDMockito.when(expenseRepositoryMock.findAllByUser(PAGEABLE))
          .thenReturn(Page.empty(PAGEABLE));

      Assertions.assertThatExceptionOfType(EntityNotFoundException.class)
          .isThrownBy(() -> expenseServiceImpl.readAllByUser(PAGEABLE))
          .withMessage("The user has no expenses");
    }

    @Test
    @DisplayName("readAllByUser must returns empty page when user has expenses but request a page without data")
    void readAllByUser_ReturnsEmptyPage_WhenUserHasExpensesButRequestAPageWithoutData() {
      BDDMockito.when(expenseRepositoryMock.findAllByUser(any()))
          .thenReturn(new PageImpl<>(Collections.emptyList(), PAGEABLE_WITH_PAGE_ONE, 4));

      Page<ExpenseResponse> actualExpenseResponsePage = expenseServiceImpl.readAllByUser(PAGEABLE_WITH_PAGE_ONE);

      Assertions.assertThat(actualExpenseResponsePage).isEmpty();
      Assertions.assertThat(actualExpenseResponsePage.getTotalElements()).isEqualTo(4L);
    }

  }

  @Nested
  @DisplayName("Tests for readByDescriptionAndUser method")
  class ReadByDescriptionAndUserMethod {

    @Test
    @DisplayName("readByDescriptionAndUser must returns Page<ExpenseResponse> when read successfully")
    void readByDescriptionAndUser_MustReturnsPageExpenseResponse_WhenReadSuccessfully() {
      BDDMockito.when(expenseRepositoryMock.findByDescriptionAndUser(eq("Aluguel"), any()))
          .thenReturn(new PageImpl<>(List.of(EXPENSE)));

      Page<ExpenseResponse> actualExpenseResponsePage = expenseServiceImpl
          .readByDescriptionAndUser("Aluguel", PAGEABLE);
      List<ExpenseResponse> actualContent = actualExpenseResponsePage.getContent();

      Assertions.assertThat(actualExpenseResponsePage).isNotEmpty();
      Assertions.assertThat(actualExpenseResponsePage.getNumberOfElements()).isEqualTo(1);
      Assertions.assertThat(actualContent.get(0).getDescription()).isEqualTo("Aluguel xpto");
      Assertions.assertThat(actualContent.get(0).getValue()).isEqualTo("1500.00");
    }

    @Test
    @DisplayName("readByDescriptionAndUser must throws EntityNotFoundException when user has no expenses")
    void readByDescriptionAndUser_ThrowsEntityNotFoundException_WhenUserHasNoExpenses() {
      BDDMockito.when(expenseRepositoryMock.findByDescriptionAndUser(eq("Supermercado"), any()))
          .thenReturn(Page.empty(PAGEABLE));

      Assertions.assertThatExceptionOfType(EntityNotFoundException.class)
          .isThrownBy(() -> expenseServiceImpl.readByDescriptionAndUser("Supermercado", PAGEABLE))
          .withMessageContaining("The user has no expenses with a description similar to ");
    }

    @Test
    @DisplayName("readByDescriptionAndUser must returns empty page when user has expenses but request a page without data")
    void readByDescriptionAndUser_ReturnsEmptyPage_WhenUserHasExpensesButRequestAPageWithoutData() {
      BDDMockito.when(expenseRepositoryMock.findByDescriptionAndUser(eq("uber"), any()))
          .thenReturn(new PageImpl<>(Collections.emptyList(), PAGEABLE_WITH_PAGE_ONE, 4));

      Page<ExpenseResponse> actualExpenseResponsePage = expenseServiceImpl
          .readByDescriptionAndUser("uber", PAGEABLE_WITH_PAGE_ONE);

      Assertions.assertThat(actualExpenseResponsePage).isEmpty();
      Assertions.assertThat(actualExpenseResponsePage.getTotalElements()).isEqualTo(4L);
    }

  }

  @Nested
  @DisplayName("Tests for readByIdAndUser method")
  class ReadByIdAndUserMethod {

    @Test
    @DisplayName("readByIdAndUser must returns expenseResponse when read by id and user successfully")
    void readByIdAndUser_MustReturnsExpenseResponse_WhenReadByIdAndUserSuccessfully() {
      BDDMockito.when(expenseRepositoryMock.findByIdAndUser(100_000L))
          .thenReturn(Optional.of(EXPENSE));

      ExpenseResponse actualExpenseResponse = expenseServiceImpl.readByIdAndUser(100_000L);

      Assertions.assertThat(actualExpenseResponse).isNotNull();
      Assertions.assertThat(actualExpenseResponse.getId()).isEqualTo(100_000L);
      Assertions.assertThat(actualExpenseResponse.getDescription()).isEqualTo("Aluguel xpto");
    }

    @Test
    @DisplayName("readByIdAndUser must throws EntityNotFoundException when expense with id 999_999 no exists")
    void readByIdAndUser_MustThrowsEntityNotFoundException_WhenExpenseWithId999999NoExists() {
      BDDMockito.when(expenseRepositoryMock.findByIdAndUser(999_999L))
          .thenReturn(Optional.empty());

      Assertions.assertThatExceptionOfType(EntityNotFoundException.class)
          .isThrownBy(() -> expenseServiceImpl.readByIdAndUser(999_999L))
          .withMessage("Expense not found");
    }

  }

  @Nested
  @DisplayName("Tests for readByYearAndMonthAndUser method")
  class ReadByYearAndMonthAndUserMethod {

    @Test
    @DisplayName("readByYearAndMonthAndUser must returns Page<ExpenseResponse> when found successfully")
    void readByYearAndMonthAndUser_MustReturnsPageExpenseResponse_WhenFoundSuccessfully() {
      BDDMockito.when(expenseRepositoryMock.findByYearAndMonthAndUser(2023, 2, PAGEABLE))
          .thenReturn(new PageImpl<>(List.of(EXPENSE)));

      Page<ExpenseResponse> actualExpenseResponsePage = expenseServiceImpl
          .readByYearAndMonthAndUser(2023, 2, PAGEABLE);
      List<ExpenseResponse> actualContent = actualExpenseResponsePage.getContent();

      Assertions.assertThat(actualExpenseResponsePage).isNotEmpty();
      Assertions.assertThat(actualExpenseResponsePage.getNumberOfElements()).isEqualTo(1);
      Assertions.assertThat(actualContent.get(0).getDate().getYear()).isEqualTo(2023);
      Assertions.assertThat(actualContent.get(0).getDate().getMonthValue()).isEqualTo(2);
    }

    @Test
    @DisplayName("readByYearAndMonthAndUser must throws EntityNotFoundException when has no expenses")
    void readByYearAndMonthAndUser_MustThrowsEntityNotFoundException_WhenHasNoExpenses() {
      BDDMockito.when(expenseRepositoryMock.findByYearAndMonthAndUser(2023, 3, PAGEABLE))
          .thenReturn(Page.empty());

      Assertions.assertThatExceptionOfType(EntityNotFoundException.class)
          .isThrownBy(() -> expenseServiceImpl.readByYearAndMonthAndUser(2023, 3, PAGEABLE))
          .withMessage("Has no expenses for year 2023 and month MARCH");
    }

    @Test
    @DisplayName("readByYearAndMonthAndUser must returns empty page when user has expenses but request a page without data")
    void readByYearAndMonthAndUser_MustReturnsEmptyPage_WhenUserHasExpensesButRequestAPageWithoutData() {
      BDDMockito.when(expenseRepositoryMock.findByYearAndMonthAndUser(2023, 2, PAGEABLE_WITH_PAGE_ONE))
          .thenReturn(new PageImpl<>(Collections.emptyList(), PAGEABLE_WITH_PAGE_ONE, 4));

      Page<ExpenseResponse> actualExpenseResponsePage = expenseServiceImpl
          .readByYearAndMonthAndUser(2023, 2, PAGEABLE_WITH_PAGE_ONE);

      Assertions.assertThat(actualExpenseResponsePage).isEmpty();
      Assertions.assertThat(actualExpenseResponsePage.getTotalElements()).isEqualTo(4L);
    }

  }

  @Nested
  @DisplayName("Tests for update method")
  class UpdateMethod {

    @Test
    @DisplayName("update must returns ExpenseResponse when update successfully")
    void update_MustReturnsExpenseResponse_WhenUpdateSuccessfully() {
      Expense expense = Expense.builder()
          .id(100_000L)
          .description("Aluguel xpto")
          .category(Category.MORADIA)
          .value(new BigDecimal("1500.00"))
          .date(LocalDate.parse("2023-02-05"))
          .user(USER)
          .build();

      BDDMockito.when(expenseRepositoryMock.findByIdAndUser(100_000L))
          .thenReturn(Optional.of(expense));

      ExpenseRequest expenseRequest = ExpenseRequest.builder()
          .description("Aluguel xpto updated")
          .value(new BigDecimal("1750.00"))
          .date("2023-02-05")
          .category("MORADIA")
          .build();

      ExpenseResponse actualExpenseResponse = expenseServiceImpl.update(100_000L, expenseRequest);

      Assertions.assertThat(actualExpenseResponse).isNotNull();
      Assertions.assertThat(actualExpenseResponse.getId()).isEqualTo(100_000L);
      Assertions.assertThat(actualExpenseResponse.getDescription()).isEqualTo("Aluguel xpto updated");
      Assertions.assertThat(actualExpenseResponse.getValue()).isEqualTo("1750.00");
    }

    @Test
    @DisplayName("update must throws EntityNotFoundException when expense no exists")
    void update_MustThrowsEntityNotFoundException_WhenExpenseNoExists() {
      BDDMockito.when(expenseRepositoryMock.findByIdAndUser(999_999L))
          .thenReturn(Optional.empty());


      ExpenseRequest expenseRequest = ExpenseRequest.builder()
          .description("Aluguel xpto updated")
          .value(new BigDecimal("1750.00"))
          .date("2023-02-05")
          .category("MORADIA")
          .build();

      Assertions.assertThatExceptionOfType(EntityNotFoundException.class)
          .isThrownBy(() -> expenseServiceImpl.update(999_999L, expenseRequest))
          .withMessage("Expense not found");
    }

  }

  @Nested
  @DisplayName("Tests for deleteById method")
  class DeleteByIdMethod {

    @Test
    @DisplayName("deleteById must call ExpenseRepository#delete when delete successfully")
    void deleteById_MustCallExpenseRepositoryDelete_WhenDeleteSuccessfully() {
      BDDMockito.when(expenseRepositoryMock.findByIdAndUser(100_000L))
          .thenReturn(Optional.of(EXPENSE));

      expenseServiceImpl.deleteById(100_000L);

      BDDMockito.verify(expenseRepositoryMock).delete(any());
    }

    @Test
    @DisplayName("deleteById must throws EntityNotFoundException when no exists expense with id 999_999")
    void deleteById_ThrowsEntityNotFoundException_WhenNoExistsExpenseWithId999999() {
      BDDMockito.when(expenseRepositoryMock.findByIdAndUser(999_999L))
          .thenReturn(Optional.empty());

      Assertions.assertThatExceptionOfType(EntityNotFoundException.class)
          .isThrownBy(() -> expenseServiceImpl.deleteById(999_999L))
          .withMessage("Expense not found");
    }

  }

  @Nested
  @DisplayName("Tests for getValuesByCategoryOnMonthAndYearByUser method")
  class GetValuesByCategoryOnMonthAndYearByUserMethod {

    @Test
    @DisplayName("getValuesByCategoryOnMonthAndYearByUser must return List<ValueByCategoryResponse> when get successfully")
    void getValuesByCategoryOnMonthAndYearByUser_MustReturnListValueByCategoryResponse_WhenGetSuccessfully() {
      ValueByCategoryResponse valueByCategoryResponse = ValueByCategoryResponse.builder()
          .category(Category.MORADIA)
          .value(new BigDecimal("1500.00"))
          .build();

      BDDMockito.when(expenseRepositoryMock.getValueByCategoryAndMonthAndYearAndUser(2023, 2))
          .thenReturn(List.of(valueByCategoryResponse));

      List<ValueByCategoryResponse> actualValueByCategoryResponseList = expenseServiceImpl
          .getValuesByCategoryOnMonthAndYearByUser(2023, 2);

      Assertions.assertThat(actualValueByCategoryResponseList).isNotNull().isNotEmpty().hasSize(1);
      Assertions.assertThat(actualValueByCategoryResponseList.get(0).getCategory().name()).isEqualTo("MORADIA");
      Assertions.assertThat(actualValueByCategoryResponseList.get(0).getValue()).isEqualTo("1500.00");
    }

    @Test
    @DisplayName("getValuesByCategoryOnMonthAndYearByUser must return empty List when has no expenses for year 2023 and month 3")
    void getValuesByCategoryOnMonthAndYearByUser_MustReturnEmptyList_WhenHasNoExpensesForYear2023AndMonth3() {
      BDDMockito.when(expenseRepositoryMock.getValueByCategoryAndMonthAndYearAndUser(2023, 3))
          .thenReturn(Collections.emptyList());

      List<ValueByCategoryResponse> actualValueByCategoryResponseList = expenseServiceImpl
          .getValuesByCategoryOnMonthAndYearByUser(2023, 3);

      Assertions.assertThat(actualValueByCategoryResponseList).isNotNull().isEmpty();
    }

  }

}
