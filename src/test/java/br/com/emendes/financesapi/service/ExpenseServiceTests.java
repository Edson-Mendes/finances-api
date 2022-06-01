package br.com.emendes.financesapi.service;

import java.util.List;
import java.util.Optional;

import javax.persistence.NoResultException;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.emendes.financesapi.controller.dto.ExpenseDto;
import br.com.emendes.financesapi.controller.form.ExpenseForm;
import br.com.emendes.financesapi.creator.ExpenseCreator;
import br.com.emendes.financesapi.creator.ExpenseFormCreator;
import br.com.emendes.financesapi.model.Expense;
import br.com.emendes.financesapi.model.User;
import br.com.emendes.financesapi.repository.ExpenseRepository;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests for ExpenseService")
public class ExpenseServiceTests {

  @InjectMocks
  private ExpenseService expenseService;

  @Mock
  private ExpenseRepository expenseRepositoryMock;

  private final Long USER_ID = 100l;
  private final Long NON_EXISTING_USER_ID = 999l;
  private final Long NON_EXISTING_EXPENSE_ID = 99999l;
  private final Pageable PAGEABLE = PageRequest.of(0, 10, Direction.DESC, "date");

  @BeforeEach
  public void setUp() {
    ExpenseForm expenseForm = ExpenseFormCreator.validExpenseForm();
    Expense expenseToBeSaved = ExpenseCreator.validExpenseWithUser(new User(USER_ID));
    Expense expenseSaved = ExpenseCreator.expenseWithAllArgs();

    PageImpl<Expense> pageExpense = new PageImpl<>(List.of(expenseSaved));

    BDDMockito.when(expenseRepositoryMock.findByDescriptionAndMonthAndYearAndUserId(
        expenseForm.getDescription(),
        expenseForm.parseDateToLocalDate().getMonthValue(),
        expenseForm.parseDateToLocalDate().getYear(),
        USER_ID)).thenReturn(Optional.empty());

    BDDMockito.when(expenseRepositoryMock.save(expenseToBeSaved))
        .thenReturn(expenseSaved);

    BDDMockito.when(expenseRepositoryMock.findByUserId(USER_ID, PAGEABLE))
        .thenReturn(pageExpense);

    BDDMockito.when(expenseRepositoryMock.findByUserId(NON_EXISTING_USER_ID, PAGEABLE))
        .thenReturn(Page.empty(PAGEABLE));

    BDDMockito.when(expenseRepositoryMock.findByDescriptionAndUserId("lina", USER_ID, PAGEABLE))
        .thenReturn(pageExpense);

    BDDMockito.when(expenseRepositoryMock.findByDescriptionAndUserId("lina", NON_EXISTING_USER_ID, PAGEABLE))
        .thenReturn(Page.empty(PAGEABLE));

    BDDMockito.when(expenseRepositoryMock.findByIdAndUserId(expenseSaved.getId(), USER_ID))
        .thenReturn(Optional.of(expenseSaved));

    BDDMockito.when(expenseRepositoryMock.findByIdAndUserId(NON_EXISTING_EXPENSE_ID, USER_ID))
        .thenReturn(Optional.empty());

    BDDMockito.when(expenseRepositoryMock.findByDescriptionAndMonthAndYearAndUserIdAndNotId(
        expenseForm.getDescription(),
        expenseForm.parseDateToLocalDate().getMonthValue(),
        expenseForm.parseDateToLocalDate().getYear(),
        USER_ID,
        expenseSaved.getId())).thenReturn(Optional.empty());
  }

  @Test
  @DisplayName("Create must returns ExpenseDto when created successfully")
  void create_ReturnsExpenseDto_WhenSuccessful() {
    ExpenseForm expenseForm = ExpenseFormCreator.validExpenseForm();
    Long userId = USER_ID;

    ExpenseDto expenseDto = this.expenseService.create(expenseForm, userId);

    Assertions.assertThat(expenseDto).isNotNull();
    Assertions.assertThat(expenseDto.getDescription()).isEqualTo(expenseForm.getDescription());
    Assertions.assertThat(expenseDto.getValue()).isEqualTo(expenseForm.getValue());
  }

  @Test
  @DisplayName("readAllByUser must returns page of expenseDto when successful")
  void readAllByUser_ReturnsPageOfExpenseDto_WhenSuccessful() {
    Long userId = USER_ID;

    Page<ExpenseDto> pageExpenseDto = expenseService.readAllByUser(userId, PAGEABLE);

    Assertions.assertThat(pageExpenseDto).isNotEmpty();
    Assertions.assertThat(pageExpenseDto.getNumberOfElements()).isEqualTo(1);
  }

  @Test
  @DisplayName("readAllByUser must throws NoResultException when user don't have expenses")
  void readAllByUser_ThrowsNoResultException_WhenUserDontHaveExpenses() {
    Long userId = NON_EXISTING_USER_ID;

    Assertions.assertThatExceptionOfType(NoResultException.class)
        .isThrownBy(() -> expenseService.readAllByUser(userId, PAGEABLE))
        .withMessage("O usuário não possui despesas");
  }

  @Test
  @DisplayName("readByDescriptionAndUser must returns page of expenseDto when successful")
  void readByDescriptionAndUser_ReturnsPageOfExpenseDto_WhenSuccessful() {
    Long userId = USER_ID;
    String description = "lina";

    Page<ExpenseDto> pageExpenseDto = expenseService.readByDescriptionAndUser(description, userId, PAGEABLE);

    Assertions.assertThat(pageExpenseDto).isNotEmpty();
    Assertions.assertThat(pageExpenseDto.getNumberOfElements()).isEqualTo(1);
  }

  @Test
  @DisplayName("readByDescriptionAndUser must throws NoResultException when user don't have expenses")
  void readByDescriptionAndUser_ThrowsNoResultException_WhenUserDontHaveExpenses() {
    Long userId = NON_EXISTING_USER_ID;
    String description = "lina";

    Assertions.assertThatExceptionOfType(NoResultException.class)
        .isThrownBy(() -> expenseService.readByDescriptionAndUser(description, userId, PAGEABLE))
        .withMessageContaining("O usuário não possui despesas com descrição similar a ");
  }

  @Test
  @DisplayName("readByIdAndUser must returns optional expenseDto when successful")
  void readByIdAndUser_ReturnsOptionalExpenseDto_WhenSuccessful() {
    Long userId = USER_ID;
    Long id = ExpenseCreator.expenseWithAllArgs().getId();

    ExpenseDto expenseDto = expenseService.readByIdAndUser(id, userId);

    Assertions.assertThat(expenseDto).isNotNull();
    Assertions.assertThat(expenseDto.getId()).isEqualTo(id);
  }

  @Test
  @DisplayName("readByIdAndUser must throws NoResultException when expenseId don't exists")
  void readByIdAndUser_ThrowsNoResultException_WhenExpenseIdDontExists() {
    Long userId = USER_ID;
    Long id = NON_EXISTING_EXPENSE_ID;

    Assertions.assertThatExceptionOfType(NoResultException.class)
        .isThrownBy(() -> expenseService.readByIdAndUser(id, userId))
        .withMessage("Nenhuma despesa com esse id para esse usuário");
  }

  @Test
  @DisplayName("update must returns ExpenseDto updated when successful")
  void update_ReturnsExpenseDtoUpdated_WhenSuccessful() {
    Long id = ExpenseCreator.expenseWithAllArgs().getId();
    ExpenseForm expenseForm = ExpenseFormCreator.validExpenseForm();
    Long userId = USER_ID;

    ExpenseDto updateExpense = expenseService.update(id, expenseForm, userId);

    Assertions.assertThat(updateExpense).isNotNull();
    Assertions.assertThat(updateExpense.getId()).isEqualTo(id);
    Assertions.assertThat(updateExpense.getDescription()).isEqualTo(expenseForm.getDescription());
  }

}
