package br.com.emendes.financesapi.unit.service;

import br.com.emendes.financesapi.dto.request.ExpenseRequest;
import br.com.emendes.financesapi.dto.response.ExpenseResponse;
import br.com.emendes.financesapi.dto.response.ValueByCategoryResponse;
import br.com.emendes.financesapi.exception.EntityNotFoundException;
import br.com.emendes.financesapi.exception.UserIsNotAuthenticatedException;
import br.com.emendes.financesapi.mapper.ExpenseMapper;
import br.com.emendes.financesapi.model.Category;
import br.com.emendes.financesapi.model.entity.Expense;
import br.com.emendes.financesapi.repository.ExpenseRepository;
import br.com.emendes.financesapi.service.impl.ExpenseServiceImpl;
import br.com.emendes.financesapi.util.component.CurrentAuthenticationComponent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static br.com.emendes.financesapi.util.constant.ConstantForTesting.PAGEABLE;
import static br.com.emendes.financesapi.util.constant.ConstantForTesting.PAGEABLE_WITH_PAGE_ONE;
import static br.com.emendes.financesapi.util.faker.ExpenseFaker.*;
import static br.com.emendes.financesapi.util.faker.UserFaker.user;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * ExpenseServiceImpl unit tests.
 */
@ExtendWith(SpringExtension.class)
@DisplayName("Unit tests for ExpenseServiceImpl")
class ExpenseServiceImplTest {

  @InjectMocks
  private ExpenseServiceImpl expenseServiceImpl;

  @Mock
  private ExpenseRepository expenseRepositoryMock;
  @Mock
  private CurrentAuthenticationComponent currentAuthenticationComponentMock;
  @Mock
  private ExpenseMapper expenseMapperMock;

  @Nested
  @DisplayName("Tests for create method")
  class CreateMethod {

    @Test
    @DisplayName("create must returns ExpenseResponse when create successfully")
    void create_MustReturnsExpenseResponse_WhenCreateSuccessfully() {
      when(currentAuthenticationComponentMock.getCurrentUser()).thenReturn(user());
      when(expenseMapperMock.toExpense(any())).thenReturn(expense());
      when(expenseRepositoryMock.save(any(Expense.class))).thenReturn(expense());
      when(expenseMapperMock.toExpenseResponse(any())).thenReturn(expenseResponse());

      ExpenseRequest expenseRequest = ExpenseRequest.builder()
          .description("Aluguel xpto")
          .value(new BigDecimal("1500.00"))
          .date("2023-02-05")
          .category("MORADIA")
          .build();

      ExpenseResponse actualExpenseResponse = expenseServiceImpl.create(expenseRequest);

      assertThat(actualExpenseResponse).isNotNull();
      assertThat(actualExpenseResponse.getDescription()).isEqualTo("Aluguel xpto");
      assertThat(actualExpenseResponse.getValue()).isEqualTo(new BigDecimal("1500.00"));
      assertThat(actualExpenseResponse.getDate()).isEqualTo("2023-02-05");
    }

    @Test
    @DisplayName("create must throws UserIsNotAuthenticatedException when user is not authenticated")
    void create_ThrowsUserIsNotAuthenticatedException_WhenUserIsNotAuthenticated() {
      when(currentAuthenticationComponentMock.getCurrentUser())
          .thenThrow(new UserIsNotAuthenticatedException("User is not authenticate"));

      ExpenseRequest expenseRequest = ExpenseRequest.builder()
          .description("Aluguel xpto")
          .value(new BigDecimal("1500.00"))
          .date("2023-02-05")
          .category("MORADIA")
          .build();

      assertThatExceptionOfType(UserIsNotAuthenticatedException.class)
          .isThrownBy(() -> expenseServiceImpl.create(expenseRequest))
          .withMessage("User is not authenticate");
    }

  }

  @Nested
  @DisplayName("Tests for readAllByUser method")
  class ReadAllByUserMethod {

    @Test
    @DisplayName("readAllByUser must returns Page<ExpenseResponse> when read all by user successfully")
    void readAllByUser_MustReturnsPageExpenseResponse_WhenReadAllByUserSuccessfully() {
      when(currentAuthenticationComponentMock.getCurrentUser()).thenReturn(user());
      when(expenseRepositoryMock.findAllByUser(any(), any()))
          .thenReturn(new PageImpl<>(expenseList()));
      when(expenseMapperMock.toExpenseResponse(any())).thenReturn(expenseResponse());

      Page<ExpenseResponse> actualExpenseResponsePage = expenseServiceImpl.readAllByUser(PAGEABLE);
      List<ExpenseResponse> actualContent = actualExpenseResponsePage.getContent();

      assertThat(actualExpenseResponsePage).isNotEmpty();
      assertThat(actualExpenseResponsePage.getNumberOfElements()).isEqualTo(1);
      assertThat(actualContent.get(0).getDescription()).isEqualTo("Aluguel xpto");
      assertThat(actualContent.get(0).getValue()).isEqualTo("1500.00");
    }

    @Test
    @DisplayName("readAllByUser must returns empty page when user has expenses but request a page without data")
    void readAllByUser_ReturnsEmptyPage_WhenUserHasExpensesButRequestAPageWithoutData() {
      when(currentAuthenticationComponentMock.getCurrentUser()).thenReturn(user());
      when(expenseRepositoryMock.findAllByUser(any(), any()))
          .thenReturn(new PageImpl<>(Collections.emptyList(), PAGEABLE_WITH_PAGE_ONE, 4));

      Page<ExpenseResponse> actualExpenseResponsePage = expenseServiceImpl.readAllByUser(PAGEABLE_WITH_PAGE_ONE);

      assertThat(actualExpenseResponsePage).isEmpty();
      assertThat(actualExpenseResponsePage.getTotalElements()).isEqualTo(4L);
    }

    @Test
    @DisplayName("readAllByUser must throws EntityNotFoundException when user has no expenses")
    void readAllByUser_ThrowsEntityNotFoundException_WhenUserHasNoExpenses() {
      when(currentAuthenticationComponentMock.getCurrentUser()).thenReturn(user());
      when(expenseRepositoryMock.findAllByUser(any(), any()))
          .thenReturn(Page.empty(PAGEABLE));

      assertThatExceptionOfType(EntityNotFoundException.class)
          .isThrownBy(() -> expenseServiceImpl.readAllByUser(PAGEABLE))
          .withMessage("The user has no expenses");
    }

    @Test
    @DisplayName("readAllByUser must throws UserIsNotAuthenticatedException when user is not authenticated")
    void readAllByUser_ThrowsUserIsNotAuthenticatedException_WhenUserIsNotAuthenticated() {
      when(currentAuthenticationComponentMock.getCurrentUser())
          .thenThrow(new UserIsNotAuthenticatedException("User is not authenticate"));

      assertThatExceptionOfType(UserIsNotAuthenticatedException.class)
          .isThrownBy(() -> expenseServiceImpl.readAllByUser(PAGEABLE))
          .withMessage("User is not authenticate");
    }

  }

  @Nested
  @DisplayName("Tests for readByDescriptionAndUser method")
  class ReadByDescriptionAndUserMethod {

    @Test
    @DisplayName("readByDescriptionAndUser must returns Page<ExpenseResponse> when read successfully")
    void readByDescriptionAndUser_MustReturnsPageExpenseResponse_WhenReadSuccessfully() {
      when(currentAuthenticationComponentMock.getCurrentUser()).thenReturn(user());
      when(expenseRepositoryMock.findByDescriptionAndUser(eq("Aluguel"), any(), any()))
          .thenReturn(new PageImpl<>(expenseList(), PAGEABLE, 1));
      when(expenseMapperMock.toExpenseResponse(any())).thenReturn(expenseResponse());

      Page<ExpenseResponse> actualExpenseResponsePage = expenseServiceImpl
          .readByDescriptionAndUser("Aluguel", PAGEABLE);
      List<ExpenseResponse> actualContent = actualExpenseResponsePage.getContent();

      assertThat(actualExpenseResponsePage).isNotEmpty();
      assertThat(actualExpenseResponsePage.getNumberOfElements()).isEqualTo(1);
      assertThat(actualContent.get(0).getDescription()).isEqualTo("Aluguel xpto");
      assertThat(actualContent.get(0).getValue()).isEqualTo("1500.00");
    }

    @Test
    @DisplayName("readByDescriptionAndUser must returns empty page when user has expenses but request a page without data")
    void readByDescriptionAndUser_ReturnsEmptyPage_WhenUserHasExpensesButRequestAPageWithoutData() {
      when(currentAuthenticationComponentMock.getCurrentUser()).thenReturn(user());
      when(expenseRepositoryMock.findByDescriptionAndUser(eq("uber"), any(), any()))
          .thenReturn(new PageImpl<>(Collections.emptyList(), PAGEABLE_WITH_PAGE_ONE, 4));

      Page<ExpenseResponse> actualExpenseResponsePage = expenseServiceImpl
          .readByDescriptionAndUser("uber", PAGEABLE_WITH_PAGE_ONE);

      assertThat(actualExpenseResponsePage).isEmpty();
      assertThat(actualExpenseResponsePage.getTotalElements()).isEqualTo(4L);
    }

    @Test
    @DisplayName("readByDescriptionAndUser must throws EntityNotFoundException when user has no expenses")
    void readByDescriptionAndUser_ThrowsEntityNotFoundException_WhenUserHasNoExpenses() {
      when(currentAuthenticationComponentMock.getCurrentUser()).thenReturn(user());
      when(expenseRepositoryMock.findByDescriptionAndUser(eq("Supermercado"), any(), any()))
          .thenReturn(Page.empty(PAGEABLE));

      assertThatExceptionOfType(EntityNotFoundException.class)
          .isThrownBy(() -> expenseServiceImpl.readByDescriptionAndUser("Supermercado", PAGEABLE))
          .withMessageContaining("The user has no expenses with a description similar to ");
    }

    @Test
    @DisplayName("readByDescriptionAndUser must throws UserIsNotAuthenticatedException when user is not authenticated")
    void readByDescriptionAndUser_ThrowsUserIsNotAuthenticatedException_WhenUserIsNotAuthenticated() {
      when(currentAuthenticationComponentMock.getCurrentUser())
          .thenThrow(new UserIsNotAuthenticatedException("User is not authenticate"));

      assertThatExceptionOfType(UserIsNotAuthenticatedException.class)
          .isThrownBy(() -> expenseServiceImpl.readByDescriptionAndUser("Supermercado", PAGEABLE))
          .withMessage("User is not authenticate");
    }

  }

  @Nested
  @DisplayName("Tests for readByIdAndUser method")
  class ReadByIdAndUserMethod {

    @Test
    @DisplayName("readByIdAndUser must returns expenseResponse when read by id and user successfully")
    void readByIdAndUser_MustReturnsExpenseResponse_WhenReadByIdAndUserSuccessfully() {
      when(currentAuthenticationComponentMock.getCurrentUser()).thenReturn(user());
      when(expenseRepositoryMock.findByIdAndUser(eq(100_000L), any())).thenReturn(expenseOptional());
      when(expenseMapperMock.toExpenseResponse(any())).thenReturn(expenseResponse());

      ExpenseResponse actualExpenseResponse = expenseServiceImpl.readByIdAndUser(100_000L);

      assertThat(actualExpenseResponse).isNotNull();
      assertThat(actualExpenseResponse.getId()).isEqualTo(100_000L);
      assertThat(actualExpenseResponse.getDescription()).isEqualTo("Aluguel xpto");
    }

    @Test
    @DisplayName("readByIdAndUser must throws EntityNotFoundException when expense with id 999_999 no exists")
    void readByIdAndUser_MustThrowsEntityNotFoundException_WhenExpenseWithId999999NoExists() {
      when(currentAuthenticationComponentMock.getCurrentUser()).thenReturn(user());
      when(expenseRepositoryMock.findByIdAndUser(eq(999_999L), any()))
          .thenReturn(Optional.empty());

      assertThatExceptionOfType(EntityNotFoundException.class)
          .isThrownBy(() -> expenseServiceImpl.readByIdAndUser(999_999L))
          .withMessage("Expense not found with id: 999999");
    }

    @Test
    @DisplayName("readByIdAndUser must throws UserIsNotAuthenticatedException when user is not authenticated")
    void readByIdAndUser_ThrowsUserIsNotAuthenticatedException_WhenUserIsNotAuthenticated() {
      when(currentAuthenticationComponentMock.getCurrentUser())
          .thenThrow(new UserIsNotAuthenticatedException("User is not authenticate"));

      assertThatExceptionOfType(UserIsNotAuthenticatedException.class)
          .isThrownBy(() -> expenseServiceImpl.readByIdAndUser(100_000L))
          .withMessage("User is not authenticate");
    }

  }

  @Nested
  @DisplayName("Tests for readByYearAndMonthAndUser method")
  class ReadByYearAndMonthAndUserMethod {

    @Test
    @DisplayName("readByYearAndMonthAndUser must returns Page<ExpenseResponse> when found successfully")
    void readByYearAndMonthAndUser_MustReturnsPageExpenseResponse_WhenFoundSuccessfully() {
      when(currentAuthenticationComponentMock.getCurrentUser()).thenReturn(user());
      when(expenseRepositoryMock.findByYearAndMonthAndUser(eq(2023), eq(2), any(), eq(PAGEABLE)))
          .thenReturn(new PageImpl<>(expenseList(), PAGEABLE, 1));
      when(expenseMapperMock.toExpenseResponse(any())).thenReturn(expenseResponse());

      Page<ExpenseResponse> actualExpenseResponsePage = expenseServiceImpl
          .readByYearAndMonthAndUser(2023, 2, PAGEABLE);
      List<ExpenseResponse> actualContent = actualExpenseResponsePage.getContent();

      assertThat(actualExpenseResponsePage).isNotEmpty();
      assertThat(actualExpenseResponsePage.getNumberOfElements()).isEqualTo(1);
      assertThat(actualContent.get(0).getDate().getYear()).isEqualTo(2023);
      assertThat(actualContent.get(0).getDate().getMonthValue()).isEqualTo(2);
    }

    @Test
    @DisplayName("readByYearAndMonthAndUser must returns empty page when user has expenses but request a page without data")
    void readByYearAndMonthAndUser_MustReturnsEmptyPage_WhenUserHasExpensesButRequestAPageWithoutData() {
      when(currentAuthenticationComponentMock.getCurrentUser()).thenReturn(user());
      when(expenseRepositoryMock.findByYearAndMonthAndUser(eq(2023), eq(2), any(), eq(PAGEABLE_WITH_PAGE_ONE)))
          .thenReturn(new PageImpl<>(Collections.emptyList(), PAGEABLE_WITH_PAGE_ONE, 4));

      Page<ExpenseResponse> actualExpenseResponsePage = expenseServiceImpl
          .readByYearAndMonthAndUser(2023, 2, PAGEABLE_WITH_PAGE_ONE);

      assertThat(actualExpenseResponsePage).isEmpty();
      assertThat(actualExpenseResponsePage.getTotalElements()).isEqualTo(4L);
    }

    @Test
    @DisplayName("readByYearAndMonthAndUser must throws EntityNotFoundException when has no expenses")
    void readByYearAndMonthAndUser_MustThrowsEntityNotFoundException_WhenHasNoExpenses() {
      when(currentAuthenticationComponentMock.getCurrentUser()).thenReturn(user());
      when(expenseRepositoryMock.findByYearAndMonthAndUser(eq(2023), eq(3), any(), eq(PAGEABLE)))
          .thenReturn(Page.empty());

      assertThatExceptionOfType(EntityNotFoundException.class)
          .isThrownBy(() -> expenseServiceImpl.readByYearAndMonthAndUser(2023, 3, PAGEABLE))
          .withMessage("Has no expenses for year 2023 and month MARCH");
    }

    @Test
    @DisplayName("readByYearAndMonthAndUser must throws UserIsNotAuthenticatedException when user is not authenticated")
    void readByYearAndMonthAndUser_ThrowsUserIsNotAuthenticatedException_WhenUserIsNotAuthenticated() {
      when(currentAuthenticationComponentMock.getCurrentUser())
          .thenThrow(new UserIsNotAuthenticatedException("User is not authenticate"));

      assertThatExceptionOfType(UserIsNotAuthenticatedException.class)
          .isThrownBy(() -> expenseServiceImpl.readByYearAndMonthAndUser(2023, 3, PAGEABLE))
          .withMessage("User is not authenticate");
    }

  }

  @Nested
  @DisplayName("Tests for update method")
  class UpdateMethod {

    @Test
    @DisplayName("update must returns ExpenseResponse when update successfully")
    void update_MustReturnsExpenseResponse_WhenUpdateSuccessfully() {
      when(currentAuthenticationComponentMock.getCurrentUser()).thenReturn(user());
      when(expenseRepositoryMock.findByIdAndUser(eq(100_000L), any()))
          .thenReturn(expenseOptional());
      when(expenseMapperMock.toExpenseResponse(any())).thenReturn(updatedExpenseResponse());

      ExpenseRequest expenseRequest = ExpenseRequest.builder()
          .description("Aluguel xpto updated")
          .value(new BigDecimal("1750.00"))
          .date("2023-02-08")
          .category("MORADIA")
          .build();

      ExpenseResponse actualExpenseResponse = expenseServiceImpl.update(100_000L, expenseRequest);

      verify(expenseMapperMock).merge(any(), any());
      assertThat(actualExpenseResponse).isNotNull();
      assertThat(actualExpenseResponse.getId()).isEqualTo(100_000L);
      assertThat(actualExpenseResponse.getDescription()).isEqualTo("Aluguel xpto updated");
      assertThat(actualExpenseResponse.getValue()).isEqualTo("1750.00");
    }

    @Test
    @DisplayName("update must throws EntityNotFoundException when expense no exists")
    void update_MustThrowsEntityNotFoundException_WhenExpenseNoExists() {
      when(currentAuthenticationComponentMock.getCurrentUser()).thenReturn(user());
      when(expenseRepositoryMock.findByIdAndUser(eq(999_999L), any()))
          .thenReturn(Optional.empty());

      ExpenseRequest expenseRequest = ExpenseRequest.builder()
          .description("Aluguel xpto updated")
          .value(new BigDecimal("1750.00"))
          .date("2023-02-05")
          .category("MORADIA")
          .build();

      assertThatExceptionOfType(EntityNotFoundException.class)
          .isThrownBy(() -> expenseServiceImpl.update(999_999L, expenseRequest))
          .withMessage("Expense not found with id: 999999");
    }

    @Test
    @DisplayName("update must throws UserIsNotAuthenticatedException when user is not authenticated")
    void update_ThrowsUserIsNotAuthenticatedException_WhenUserIsNotAuthenticated() {
      when(currentAuthenticationComponentMock.getCurrentUser())
          .thenThrow(new UserIsNotAuthenticatedException("User is not authenticate"));

      ExpenseRequest expenseRequest = ExpenseRequest.builder()
          .description("Aluguel xpto updated")
          .value(new BigDecimal("1750.00"))
          .date("2023-02-05")
          .category("MORADIA")
          .build();

      assertThatExceptionOfType(UserIsNotAuthenticatedException.class)
          .isThrownBy(() -> expenseServiceImpl.update(100_000L, expenseRequest))
          .withMessage("User is not authenticate");
    }

  }

  @Nested
  @DisplayName("Tests for deleteById method")
  class DeleteByIdMethod {

    @Test
    @DisplayName("deleteById must call ExpenseRepository#delete when delete successfully")
    void deleteById_MustCallExpenseRepositoryDelete_WhenDeleteSuccessfully() {
      when(currentAuthenticationComponentMock.getCurrentUser()).thenReturn(user());
      when(expenseRepositoryMock.findByIdAndUser(eq(100_000L), any()))
          .thenReturn(expenseOptional());

      expenseServiceImpl.deleteById(100_000L);

      verify(expenseRepositoryMock).delete(any());
    }

    @Test
    @DisplayName("deleteById must throws EntityNotFoundException when no exists expense with id 999_999")
    void deleteById_ThrowsEntityNotFoundException_WhenNoExistsExpenseWithId999999() {
      when(currentAuthenticationComponentMock.getCurrentUser()).thenReturn(user());
      when(expenseRepositoryMock.findByIdAndUser(eq(999_999L), any()))
          .thenReturn(Optional.empty());

      assertThatExceptionOfType(EntityNotFoundException.class)
          .isThrownBy(() -> expenseServiceImpl.deleteById(999_999L))
          .withMessage("Expense not found with id: 999999");
    }

    @Test
    @DisplayName("deleteById must throws UserIsNotAuthenticatedException when user is not authenticated")
    void deleteById_ThrowsUserIsNotAuthenticatedException_WhenUserIsNotAuthenticated() {
      when(currentAuthenticationComponentMock.getCurrentUser())
          .thenThrow(new UserIsNotAuthenticatedException("User is not authenticate"));

      assertThatExceptionOfType(UserIsNotAuthenticatedException.class)
          .isThrownBy(() -> expenseServiceImpl.deleteById(100_000L))
          .withMessage("User is not authenticate");
    }

  }

  @Nested
  @DisplayName("Tests for getValuesByCategoryOnMonthAndYearByUser method")
  class GetValuesByCategoryOnMonthAndYearByUserMethod {

    @Test
    @DisplayName("getValuesByCategoryOnMonthAndYearByUser must return List<ValueByCategoryResponse> when get successfully")
    void getValuesByCategoryOnMonthAndYearByUser_MustReturnListValueByCategoryResponse_WhenGetSuccessfully() {
      when(currentAuthenticationComponentMock.getCurrentUser()).thenReturn(user());

      when(expenseRepositoryMock.getValueByCategoryAndMonthAndYearAndUser(eq(2023), eq(2), any()))
          .thenReturn(List.of(valueByCategory(Category.MORADIA, "1500.00")));

      List<ValueByCategoryResponse> actualValueByCategoryResponseList = expenseServiceImpl
          .getValuesByCategoryOnMonthAndYearByUser(2023, 2);

      assertThat(actualValueByCategoryResponseList).isNotNull().isNotEmpty().hasSize(1);
      assertThat(actualValueByCategoryResponseList.get(0).getCategory().name()).isEqualTo("MORADIA");
      assertThat(actualValueByCategoryResponseList.get(0).getValue()).isEqualTo("1500.00");
    }

    @Test
    @DisplayName("getValuesByCategoryOnMonthAndYearByUser must return empty List when has no expenses for year 2023 and month 3")
    void getValuesByCategoryOnMonthAndYearByUser_MustReturnEmptyList_WhenHasNoExpensesForYear2023AndMonth3() {
      when(currentAuthenticationComponentMock.getCurrentUser()).thenReturn(user());
      when(expenseRepositoryMock.getValueByCategoryAndMonthAndYearAndUser(eq(2023), eq(3), any()))
          .thenReturn(Collections.emptyList());

      List<ValueByCategoryResponse> actualValueByCategoryResponseList = expenseServiceImpl
          .getValuesByCategoryOnMonthAndYearByUser(2023, 3);

      assertThat(actualValueByCategoryResponseList).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("getValuesByCategoryOnMonthAndYearByUser must throws UserIsNotAuthenticatedException when user is not authenticated")
    void getValuesByCategoryOnMonthAndYearByUser_ThrowsUserIsNotAuthenticatedException_WhenUserIsNotAuthenticated() {
      when(currentAuthenticationComponentMock.getCurrentUser())
          .thenThrow(new UserIsNotAuthenticatedException("User is not authenticate"));

      assertThatExceptionOfType(UserIsNotAuthenticatedException.class)
          .isThrownBy(() -> expenseServiceImpl.getValuesByCategoryOnMonthAndYearByUser(2023, 2))
          .withMessage("User is not authenticate");
    }

  }

}
