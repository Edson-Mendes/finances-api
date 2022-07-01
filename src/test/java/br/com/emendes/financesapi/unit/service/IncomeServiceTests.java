//package br.com.emendes.financesapi.unit.service;
//
//import java.util.List;
//import java.util.Optional;
//
//import javax.persistence.NoResultException;
//
//import br.com.emendes.financesapi.service.IncomeService;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.BDDMockito;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort.Direction;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import br.com.emendes.financesapi.controller.dto.IncomeDto;
//import br.com.emendes.financesapi.controller.form.IncomeForm;
//import br.com.emendes.financesapi.util.creator.IncomeCreator;
//import br.com.emendes.financesapi.util.creator.IncomeFormCreator;
//import br.com.emendes.financesapi.model.Income;
//import br.com.emendes.financesapi.model.User;
//import br.com.emendes.financesapi.repository.IncomeRepository;
//
//@ExtendWith(SpringExtension.class)
//@DisplayName("Tests for IncomeService")
//public class IncomeServiceTests {
//
//  @InjectMocks
//  private IncomeService incomeService;
//
//  @Mock
//  private IncomeRepository incomeRepositoryMock;
//
//  private final Long USER_ID = 100l;
//  private final Long NON_EXISTING_USER_ID = 999l;
//  private final Long NON_EXISTING_EXPENSE_ID = 99999l;
//  private final Pageable PAGEABLE = PageRequest.of(0, 10, Direction.DESC, "date");
//
//  @BeforeEach
//  public void setUp() {
//    IncomeForm incomeForm = IncomeFormCreator.validIncomeForm();
//    Income incomeToBeSaved = IncomeCreator.validIncomeWithUser(new User(USER_ID));
//    Income incomeSaved = IncomeCreator.incomeWithAllArgs();
//
//    PageImpl<Income> pageIncome = new PageImpl<>(List.of(incomeSaved));
//
//    BDDMockito.when(incomeRepositoryMock.findByDescriptionAndMonthAndYearAndUserId(
//        incomeForm.getDescription(),
//        incomeForm.parseDateToLocalDate().getMonthValue(),
//        incomeForm.parseDateToLocalDate().getYear(),
//        USER_ID)).thenReturn(Optional.empty());
//
//    BDDMockito.when(incomeRepositoryMock.save(incomeToBeSaved))
//        .thenReturn(incomeSaved);
//
//    BDDMockito.when(incomeRepositoryMock.findByUserId(USER_ID, PAGEABLE))
//        .thenReturn(pageIncome);
//
//    BDDMockito.when(incomeRepositoryMock.findByUserId(NON_EXISTING_USER_ID, PAGEABLE))
//        .thenReturn(Page.empty(PAGEABLE));
//
//    BDDMockito.when(incomeRepositoryMock.findByDescriptionAndUserId("ario", USER_ID, PAGEABLE))
//        .thenReturn(pageIncome);
//
//    BDDMockito.when(incomeRepositoryMock.findByDescriptionAndUserId("ario", NON_EXISTING_USER_ID, PAGEABLE))
//        .thenReturn(Page.empty(PAGEABLE));
//
//    BDDMockito.when(incomeRepositoryMock.findByIdAndUserId(incomeSaved.getId(), USER_ID))
//        .thenReturn(Optional.of(incomeSaved));
//
//    BDDMockito.when(incomeRepositoryMock.findByIdAndUserId(NON_EXISTING_EXPENSE_ID, USER_ID))
//        .thenReturn(Optional.empty());
//
//    BDDMockito.when(incomeRepositoryMock.findByDescriptionAndMonthAndYearAndUserIdAndNotId(
//        incomeForm.getDescription(),
//        incomeForm.parseDateToLocalDate().getMonthValue(),
//        incomeForm.parseDateToLocalDate().getYear(),
//        USER_ID,
//        incomeSaved.getId())).thenReturn(Optional.empty());
//  }
//
//  @Test
//  @DisplayName("Create must returns IncomeDto when created successfully")
//  void create_ReturnsIncomeDto_WhenSuccessful() {
//    IncomeForm incomeForm = IncomeFormCreator.validIncomeForm();
//    Long userId = USER_ID;
//
//    IncomeDto incomeDto = this.incomeService.create(incomeForm, userId);
//
//    Assertions.assertThat(incomeDto).isNotNull();
//    Assertions.assertThat(incomeDto.getDescription()).isEqualTo(incomeForm.getDescription());
//    Assertions.assertThat(incomeDto.getValue()).isEqualTo(incomeForm.getValue());
//  }
//
//  @Test
//  @DisplayName("readAllByUser must returns page of incomeDto when successful")
//  void readAllByUser_ReturnsPageOfIncomeDto_WhenSuccessful() {
//    Long userId = USER_ID;
//
//    Page<IncomeDto> pageIncomeDto = incomeService.readAllByUser(userId, PAGEABLE);
//
//    Assertions.assertThat(pageIncomeDto).isNotEmpty();
//    Assertions.assertThat(pageIncomeDto.getNumberOfElements()).isEqualTo(1);
//  }
//
//  @Test
//  @DisplayName("readAllByUser must throws NoResultException when user don't have incomes")
//  void readAllByUser_ThrowsNoResultException_WhenUserDontHaveIncomes() {
//    Long userId = NON_EXISTING_USER_ID;
//
//    Assertions.assertThatExceptionOfType(NoResultException.class)
//        .isThrownBy(() -> incomeService.readAllByUser(userId, PAGEABLE))
//        .withMessage("O usuário não possui receitas");
//  }
//
//  @Test
//  @DisplayName("readByDescriptionAndUser must returns page of incomeDto when successful")
//  void readByDescriptionAndUser_ReturnsPageOfIncomeDto_WhenSuccessful() {
//    Long userId = USER_ID;
//    String description = "ario";
//
//    Page<IncomeDto> pageIncomeDto = incomeService.readByDescriptionAndUser(description, userId, PAGEABLE);
//
//    Assertions.assertThat(pageIncomeDto).isNotEmpty();
//    Assertions.assertThat(pageIncomeDto.getNumberOfElements()).isEqualTo(1);
//  }
//
//  @Test
//  @DisplayName("readByDescriptionAndUser must throws NoResultException when user don't have incomes")
//  void readByDescriptionAndUser_ThrowsNoResultException_WhenUserDontHaveIncomes() {
//    Long userId = NON_EXISTING_USER_ID;
//    String description = "ario";
//
//    Assertions.assertThatExceptionOfType(NoResultException.class)
//        .isThrownBy(() -> incomeService.readByDescriptionAndUser(description, userId, PAGEABLE))
//        .withMessageContaining("O usuário não possui receitas com descrição similar a ");
//  }
//
//  @Test
//  @DisplayName("readByIdAndUser must returns optional incomeDto when successful")
//  void readByIdAndUser_ReturnsOptionalIncomeDto_WhenSuccessful() {
//    Long userId = USER_ID;
//    Long id = IncomeCreator.incomeWithAllArgs().getId();
//
//    IncomeDto incomeDto = incomeService.readByIdAndUser(id, userId);
//
//    Assertions.assertThat(incomeDto).isNotNull();
//    Assertions.assertThat(incomeDto.getId()).isEqualTo(id);
//  }
//
//  @Test
//  @DisplayName("readByIdAndUser must throws NoResultException when incomeId don't exists")
//  void readByIdAndUser_ThrowsNoResultException_WhenIncomeIdDontExists() {
//    Long userId = USER_ID;
//    Long id = NON_EXISTING_EXPENSE_ID;
//
//    Assertions.assertThatExceptionOfType(NoResultException.class)
//        .isThrownBy(() -> incomeService.readByIdAndUser(id, userId))
//        .withMessage("Nenhuma receita com esse id para esse usuário");
//  }
//
//  @Test
//  @DisplayName("update must returns IncomeDto updated when successful")
//  void update_ReturnsIncomeDtoUpdated_WhenSuccessful() {
//    Long id = IncomeCreator.incomeWithAllArgs().getId();
//    IncomeForm incomeForm = IncomeFormCreator.validIncomeForm();
//    Long userId = USER_ID;
//
//    IncomeDto updateIncome = incomeService.update(id, incomeForm, userId);
//
//    Assertions.assertThat(updateIncome).isNotNull();
//    Assertions.assertThat(updateIncome.getId()).isEqualTo(id);
//    Assertions.assertThat(updateIncome.getDescription()).isEqualTo(incomeForm.getDescription());
//  }
//
//}
