package br.com.emendes.financesapi.unit.service;

import br.com.emendes.financesapi.controller.dto.ExpenseDto;
import br.com.emendes.financesapi.controller.form.ExpenseForm;
import br.com.emendes.financesapi.model.Expense;
import br.com.emendes.financesapi.model.User;
import br.com.emendes.financesapi.repository.ExpenseRepository;
import br.com.emendes.financesapi.service.ExpenseService;
import br.com.emendes.financesapi.util.creator.ExpenseCreator;
import br.com.emendes.financesapi.util.creator.ExpenseFormCreator;
import br.com.emendes.financesapi.util.creator.UserCreator;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.NoResultException;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests for ExpenseService")
class ExpenseServiceTests {

  @InjectMocks
  private ExpenseService expenseService;

  @Mock
  private ExpenseRepository expenseRepositoryMock;


  private final Long NON_EXISTING_EXPENSE_ID = 99999L;
  private final Pageable PAGEABLE = PageRequest.of(0, 10, Direction.DESC, "date");
  private final Authentication AUTHENTICATION = mock(Authentication.class);
  private final SecurityContext SECURITY_CONTEXT = mock(SecurityContext.class);
  private final ExpenseForm EXPENSE_FORM = ExpenseFormCreator.validExpenseForm();

  @BeforeEach
  public void setUp() {
    final Long userId = 100L;
    Expense expenseToBeSaved = ExpenseCreator.validExpenseWithUser(new User(userId));
    Expense expenseSaved = ExpenseCreator.expenseWithAllArgs();

    PageImpl<Expense> pageExpense = new PageImpl<>(List.of(expenseSaved));

    SecurityContextHolder.setContext(SECURITY_CONTEXT);

    BDDMockito.when(expenseRepositoryMock.existsByDescriptionAndMonthAndYearAndUser(
        EXPENSE_FORM.getDescription(),
        EXPENSE_FORM.parseDateToLocalDate().getMonthValue(),
        EXPENSE_FORM.parseDateToLocalDate().getYear())).thenReturn(false);

    BDDMockito.when(expenseRepositoryMock.save(expenseToBeSaved))
        .thenReturn(expenseSaved);

    BDDMockito.when(expenseRepositoryMock.findAllByUser(PAGEABLE))
        .thenReturn(pageExpense);

    BDDMockito.when(expenseRepositoryMock.findByDescriptionAndUser("solina", PAGEABLE))
        .thenReturn(pageExpense);

    BDDMockito.when(expenseRepositoryMock.findByDescriptionAndUser("lina", PAGEABLE))
        .thenReturn(Page.empty(PAGEABLE));

    BDDMockito.when(expenseRepositoryMock.findByIdAndUser(expenseSaved.getId()))
        .thenReturn(Optional.of(expenseSaved));

    BDDMockito.when(expenseRepositoryMock.findByIdAndUser(NON_EXISTING_EXPENSE_ID))
        .thenReturn(Optional.empty());

    BDDMockito.when(expenseRepositoryMock.existsByDescriptionAndMonthAndYearAndNotIdAndUser(
        EXPENSE_FORM.getDescription(),
        EXPENSE_FORM.parseDateToLocalDate().getMonthValue(),
        EXPENSE_FORM.parseDateToLocalDate().getYear(),
        expenseSaved.getId())).thenReturn(false);

    BDDMockito.when(SECURITY_CONTEXT.getAuthentication()).thenReturn(AUTHENTICATION);
    BDDMockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
        .thenReturn(UserCreator.userWithIdAndRoles());

    BDDMockito.when(expenseRepositoryMock.findByYearAndMonthAndUser(2022, 1, PAGEABLE))
        .thenReturn(pageExpense);
    BDDMockito.when(expenseRepositoryMock.findByYearAndMonthAndUser(2000, 1, PAGEABLE))
        .thenReturn(Page.empty(PAGEABLE));
  }

  @Test
  @DisplayName("Create must returns ExpenseDto when created successfully")
  void create_ReturnsExpenseDto_WhenSuccessful() {
    ExpenseForm expenseForm = ExpenseFormCreator.validExpenseForm();
    ExpenseDto expenseDto = this.expenseService.create(expenseForm);

    Assertions.assertThat(expenseDto).isNotNull();
    Assertions.assertThat(expenseDto.getDescription()).isEqualTo(expenseForm.getDescription());
    Assertions.assertThat(expenseDto.getValue()).isEqualTo(expenseForm.getValue());
  }

  @Test
  @DisplayName("create must throws ResponseStatusException when user already has expense with this description")
  void create_MustThrowsResponseStatusException_WhenUserAlreadyHasExpenseWithThisDescription(){
    BDDMockito.when(expenseRepositoryMock.existsByDescriptionAndMonthAndYearAndUser(
        EXPENSE_FORM.getDescription(),
        EXPENSE_FORM.parseDateToLocalDate().getMonthValue(),
        EXPENSE_FORM.parseDateToLocalDate().getYear())).thenReturn(true);

    ExpenseForm expenseForm = ExpenseFormCreator.validExpenseForm();
    Assertions.assertThatExceptionOfType(ResponseStatusException.class)
        .isThrownBy(() -> this.expenseService.create(expenseForm))
        .withMessageContaining("Uma despesa com essa descrição já existe em ");
  }

  @Test
  @DisplayName("readAllByUser must returns page of expenseDto when successful")
  void readAllByUser_ReturnsPageOfExpenseDto_WhenSuccessful() {

    Page<ExpenseDto> pageExpenseDto = expenseService.readAllByUser(PAGEABLE);

    Assertions.assertThat(pageExpenseDto).isNotEmpty();
    Assertions.assertThat(pageExpenseDto.getNumberOfElements()).isEqualTo(1);
  }

  @Test
  @DisplayName("readAllByUser must throws NoResultException when user don't have expenses")
  void readAllByUser_ThrowsNoResultException_WhenUserDontHaveExpenses() {
    BDDMockito.when(expenseRepositoryMock.findAllByUser(PAGEABLE))
        .thenReturn(Page.empty(PAGEABLE));

    Assertions.assertThatExceptionOfType(NoResultException.class)
        .isThrownBy(() -> expenseService.readAllByUser(PAGEABLE))
        .withMessage("O usuário não possui despesas");
  }

  @Test
  @DisplayName("readByDescriptionAndUser must returns page of expenseDto when successful")
  void readByDescriptionAndUser_ReturnsPageOfExpenseDto_WhenSuccessful() {
    String description = "solina";

    Page<ExpenseDto> pageExpenseDto = expenseService.readByDescriptionAndUser(description, PAGEABLE);

    Assertions.assertThat(pageExpenseDto).isNotEmpty();
    Assertions.assertThat(pageExpenseDto.getNumberOfElements()).isEqualTo(1);
  }

  @Test
  @DisplayName("readByDescriptionAndUser must throws NoResultException when user don't have expenses")
  void readByDescriptionAndUser_ThrowsNoResultException_WhenUserDontHaveExpenses() {
    String description = "lina";

    Assertions.assertThatExceptionOfType(NoResultException.class)
        .isThrownBy(() -> expenseService.readByDescriptionAndUser(description, PAGEABLE))
        .withMessageContaining("O usuário não possui despesas com descrição similar a ");
  }

  @Test
  @DisplayName("readByIdAndUser must returns optional expenseDto when successful")
  void readByIdAndUser_ReturnsOptionalExpenseDto_WhenSuccessful() {
    Long id = ExpenseCreator.expenseWithAllArgs().getId();

    ExpenseDto expenseDto = expenseService.readByIdAndUser(id);

    Assertions.assertThat(expenseDto).isNotNull();
    Assertions.assertThat(expenseDto.getId()).isEqualTo(id);
  }

  @Test
  @DisplayName("readByIdAndUser must throws NoResultException when expenseId don't exists")
  void readByIdAndUser_ThrowsNoResultException_WhenExpenseIdDontExists() {
    Assertions.assertThatExceptionOfType(NoResultException.class)
        .isThrownBy(() -> expenseService.readByIdAndUser(NON_EXISTING_EXPENSE_ID))
        .withMessage(String.format("Nenhuma despesa com id = %d para esse usuário", NON_EXISTING_EXPENSE_ID));
  }

  @Test
  @DisplayName("readByYearAndMonthAndUser must returns Page<ExpenseDto> when finded successful")
  void readByYearAndMonthAndUser_ReturnsPageExpenseDto_WhenFindedSuccessful(){
    Integer month = 1;
    Integer year = 2022;

    Page<ExpenseDto> pageExpenseDto = expenseService.readByYearAndMonthAndUser(year, month, PAGEABLE);

    Assertions.assertThat(pageExpenseDto).isNotEmpty();
    Assertions.assertThat(pageExpenseDto.getNumberOfElements()).isEqualTo(1);
  }

  @Test
  @DisplayName("readByYearAndMonthAndUser must throws NoResultException when don't has expenses")
  void readByYearAndMonthAndUser_ThrowsNoResultException_WhenDontHasExpenses(){
    Integer month = 1;
    Integer year = 2000;

    Assertions.assertThatExceptionOfType(NoResultException.class)
        .isThrownBy(() -> expenseService.readByYearAndMonthAndUser(year, month, PAGEABLE))
        .withMessage(String.format("Não há despesas para o ano %d e mês %d", year, month));
  }

  @Test
  @DisplayName("update must returns ExpenseDto updated when successful")
  void update_ReturnsExpenseDtoUpdated_WhenSuccessful() {
    Long id = ExpenseCreator.expenseWithAllArgs().getId();
    ExpenseForm expenseForm = ExpenseFormCreator.validExpenseForm();

    ExpenseDto updateExpense = expenseService.update(id, expenseForm);

    Assertions.assertThat(updateExpense).isNotNull();
    Assertions.assertThat(updateExpense.getId()).isEqualTo(id);
    Assertions.assertThat(updateExpense.getDescription()).isEqualTo(expenseForm.getDescription());
  }

  @Test
  @DisplayName("update must throws ResponseStatusException when user already has another expense with same description")
  void update_MustThrowsResponseStatusException_WhenUserHasAnotherExpenseWithSameDescription() {
    BDDMockito.when(expenseRepositoryMock.existsByDescriptionAndMonthAndYearAndNotIdAndUser(
        EXPENSE_FORM.getDescription(),
        EXPENSE_FORM.parseDateToLocalDate().getMonthValue(),
        EXPENSE_FORM.parseDateToLocalDate().getYear(),
        1000L)).thenReturn(true);

    Long id = ExpenseCreator.expenseWithAllArgs().getId();
    ExpenseForm expenseForm = ExpenseFormCreator.validExpenseForm();

    Assertions.assertThatExceptionOfType(ResponseStatusException.class)
        .isThrownBy(() -> expenseService.update(id, expenseForm))
        .withMessageContaining("Outra despesa com essa descrição já existe em ");
  }

  @Test
  @DisplayName("update must throws NoResultException when expense don't exists")
  void update_ThrowsNoResultException_WhenExpenseDontExists() {
    ExpenseForm expenseForm = ExpenseFormCreator.validExpenseForm();

    Assertions.assertThatExceptionOfType(NoResultException.class)
        .isThrownBy(() -> expenseService.update(NON_EXISTING_EXPENSE_ID, expenseForm))
        .withMessage(String.format("Nenhuma despesa com id = %d para esse usuário", NON_EXISTING_EXPENSE_ID));
  }


  @Test
  @DisplayName("readByYearAndMonthAndUser must throws NoResultException when don't has expenses")
  void deleteById_ThrowsNoResultException_WhenExpenseDontExists(){
    Assertions.assertThatExceptionOfType(NoResultException.class)
        .isThrownBy(() -> expenseService.deleteById(NON_EXISTING_EXPENSE_ID))
        .withMessage(String.format("Nenhuma despesa com id = %d para esse usuário", NON_EXISTING_EXPENSE_ID));
  }

}
