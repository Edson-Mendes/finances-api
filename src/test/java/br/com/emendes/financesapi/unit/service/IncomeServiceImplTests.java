package br.com.emendes.financesapi.unit.service;

import br.com.emendes.financesapi.dto.response.IncomeResponse;
import br.com.emendes.financesapi.dto.request.IncomeRequest;
import br.com.emendes.financesapi.model.entity.Income;
import br.com.emendes.financesapi.model.entity.User;
import br.com.emendes.financesapi.repository.IncomeRepository;
import br.com.emendes.financesapi.service.impl.IncomeServiceImpl;
import br.com.emendes.financesapi.util.creator.IncomeCreator;
import br.com.emendes.financesapi.util.creator.IncomeFormCreator;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests for IncomeService")
class IncomeServiceImplTests {

  @InjectMocks
  private IncomeServiceImpl incomeServiceImpl;

  @Mock
  private IncomeRepository incomeRepositoryMock;

  private final Long NON_EXISTING_INCOME_ID = 99999L;
  private final Pageable PAGEABLE = PageRequest.of(0, 10, Direction.DESC, "date");
  private final Pageable PAGEABLE_WITH_PAGE_ONE = PageRequest.of(1, 10, Direction.DESC, "date");
  private final Authentication AUTHENTICATION = mock(Authentication.class);
  private final SecurityContext SECURITY_CONTEXT = mock(SecurityContext.class);
  private final IncomeRequest INCOME_FORM = IncomeFormCreator.validIncomeForm();
  @BeforeEach
  public void setUp() {
    Income incomeToBeSaved = IncomeCreator.validIncomeWithUser(new User(100L));
    Income incomeSaved = IncomeCreator.incomeWithAllArgs();

    PageImpl<Income> pageIncome = new PageImpl<>(List.of(incomeSaved));
    PageImpl<Income> pageOneEmpty = new PageImpl<>(Collections.emptyList(), PAGEABLE_WITH_PAGE_ONE, 4L);

    SecurityContextHolder.setContext(SECURITY_CONTEXT);

    BDDMockito.when(incomeRepositoryMock.existsByDescriptionAndMonthAndYearAndUser(
        INCOME_FORM.getDescription(),
        INCOME_FORM.parseDateToLocalDate().getMonthValue(),
        INCOME_FORM.parseDateToLocalDate().getYear())).thenReturn(false);

    BDDMockito.when(incomeRepositoryMock.save(incomeToBeSaved))
        .thenReturn(incomeSaved);

    BDDMockito.when(incomeRepositoryMock.findAllByUser(PAGEABLE))
        .thenReturn(pageIncome);

    BDDMockito.when(incomeRepositoryMock.findByDescriptionAndUser("ario", PAGEABLE))
        .thenReturn(pageIncome);

    BDDMockito.when(incomeRepositoryMock.findByDescriptionAndUser("lario", PAGEABLE))
        .thenReturn(Page.empty(PAGEABLE));

    BDDMockito.when(incomeRepositoryMock.findByDescriptionAndUser("venda", PAGEABLE_WITH_PAGE_ONE))
        .thenReturn(pageOneEmpty);

    BDDMockito.when(incomeRepositoryMock.findAllByUser(PAGEABLE_WITH_PAGE_ONE))
        .thenReturn(pageOneEmpty);

    BDDMockito.when(incomeRepositoryMock.findByIdAndUser(incomeSaved.getId()))
        .thenReturn(Optional.of(incomeSaved));

    BDDMockito.when(incomeRepositoryMock.findByIdAndUser(NON_EXISTING_INCOME_ID))
        .thenReturn(Optional.empty());

    BDDMockito.when(incomeRepositoryMock.existsByDescriptionAndMonthAndYearAndNotIdAndUser(
        INCOME_FORM.getDescription(),
        INCOME_FORM.parseDateToLocalDate().getMonthValue(),
        INCOME_FORM.parseDateToLocalDate().getYear(),
        incomeSaved.getId())).thenReturn(false);

    BDDMockito.when(SECURITY_CONTEXT.getAuthentication()).thenReturn(AUTHENTICATION);
    BDDMockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
        .thenReturn(UserCreator.userWithIdAndRoles());

    BDDMockito.when(incomeRepositoryMock.findByYearAndMonthAndUser(2022, 1, PAGEABLE))
        .thenReturn(pageIncome);

    BDDMockito.when(incomeRepositoryMock.findByYearAndMonthAndUser(2000, 1, PAGEABLE))
        .thenReturn(Page.empty(PAGEABLE));

    BDDMockito.when(incomeRepositoryMock.findByYearAndMonthAndUser(2023, 9, PAGEABLE_WITH_PAGE_ONE))
        .thenReturn(pageOneEmpty);
  }

  @Test
  @DisplayName("Create must returns IncomeDto when created successfully")
  void create_ReturnsIncomeDto_WhenSuccessful() {
    IncomeRequest incomeRequest = IncomeFormCreator.validIncomeForm();

    IncomeResponse incomeResponse = this.incomeServiceImpl.create(incomeRequest);

    Assertions.assertThat(incomeResponse).isNotNull();
    Assertions.assertThat(incomeResponse.getDescription()).isEqualTo(incomeRequest.getDescription());
    Assertions.assertThat(incomeResponse.getValue()).isEqualTo(incomeRequest.getValue());
  }

  @Test
  @DisplayName("create must throws ResponseStatusException when user already has income with this description")
  void create_MustThrowsResponseStatusException_WhenUserAlreadyHasIncomeWithThisDescription(){
    BDDMockito.when(incomeRepositoryMock.existsByDescriptionAndMonthAndYearAndUser(
        INCOME_FORM.getDescription(),
        INCOME_FORM.parseDateToLocalDate().getMonthValue(),
        INCOME_FORM.parseDateToLocalDate().getYear())).thenReturn(true);

    IncomeRequest incomeRequest = IncomeFormCreator.validIncomeForm();
    Assertions.assertThatExceptionOfType(ResponseStatusException.class)
        .isThrownBy(() -> this.incomeServiceImpl.create(incomeRequest))
        .withMessageContaining("Uma receita com essa descrição já existe em ");
  }

  @Test
  @DisplayName("readAllByUser must returns page of incomeDto when successful")
  void readAllByUser_ReturnsPageOfIncomeDto_WhenSuccessful() {
    Page<IncomeResponse> pageIncomeDto = incomeServiceImpl.readAllByUser(PAGEABLE);

    Assertions.assertThat(pageIncomeDto).isNotEmpty();
    Assertions.assertThat(pageIncomeDto.getNumberOfElements()).isEqualTo(1);
  }

  @Test
  @DisplayName("readAllByUser must throws NoResultException when user don't have incomes")
  void readAllByUser_ThrowsNoResultException_WhenUserDontHaveIncomes() {
    BDDMockito.when(incomeRepositoryMock.findAllByUser(PAGEABLE))
        .thenReturn(Page.empty(PAGEABLE));

    Assertions.assertThatExceptionOfType(NoResultException.class)
        .isThrownBy(() -> incomeServiceImpl.readAllByUser(PAGEABLE))
        .withMessage("O usuário não possui receitas");
  }

  @Test
  @DisplayName("readAllByUser must returns empty page when user has incomes but request a page without data")
  void readAllByUser_ReturnsEmptyPage_WhenUserHasIncomesButRequestAPageWithoutData(){
    Page<IncomeResponse> pageIncomeDto = incomeServiceImpl.readAllByUser(PAGEABLE_WITH_PAGE_ONE);

    Assertions.assertThat(pageIncomeDto).isEmpty();
    Assertions.assertThat(pageIncomeDto.getTotalElements()).isEqualTo(4L);
  }

  @Test
  @DisplayName("readByDescriptionAndUser must returns page of incomeDto when successful")
  void readByDescriptionAndUser_ReturnsPageOfIncomeDto_WhenSuccessful() {
    String description = "ario";

    Page<IncomeResponse> pageIncomeDto = incomeServiceImpl.readByDescriptionAndUser(description, PAGEABLE);

    Assertions.assertThat(pageIncomeDto).isNotEmpty();
    Assertions.assertThat(pageIncomeDto.getNumberOfElements()).isEqualTo(1);
  }

  @Test
  @DisplayName("readByDescriptionAndUser must throws NoResultException when user don't have incomes")
  void readByDescriptionAndUser_ThrowsNoResultException_WhenUserDontHaveIncomes() {
    String description = "lario";

    Assertions.assertThatExceptionOfType(NoResultException.class)
        .isThrownBy(() -> incomeServiceImpl.readByDescriptionAndUser(description, PAGEABLE))
        .withMessageContaining("O usuário não possui receitas com descrição similar a ");
  }

  @Test
  @DisplayName("readByDescriptionAndUser must returns empty page when user has incomes but request a page without data")
  void readByDescriptionAndUser_ReturnsEmptyPage_WhenUserHasIncomesButRequestAPageWithoutData(){
    String description = "venda";
    Page<IncomeResponse> pageIncomeDto = incomeServiceImpl.readByDescriptionAndUser(description, PAGEABLE_WITH_PAGE_ONE);

    Assertions.assertThat(pageIncomeDto).isNotNull().isEmpty();
    Assertions.assertThat(pageIncomeDto.getTotalElements()).isEqualTo(4L);
  }

  @Test
  @DisplayName("readByIdAndUser must returns optional incomeDto when successful")
  void readByIdAndUser_ReturnsOptionalIncomeDto_WhenSuccessful() {
    Long id = IncomeCreator.incomeWithAllArgs().getId();

    IncomeResponse incomeResponse = incomeServiceImpl.readByIdAndUser(id);

    Assertions.assertThat(incomeResponse).isNotNull();
    Assertions.assertThat(incomeResponse.getId()).isEqualTo(id);
  }

  @Test
  @DisplayName("readByIdAndUser must throws NoResultException when incomeId don't exists")
  void readByIdAndUser_ThrowsNoResultException_WhenIncomeIdDontExists() {
    Long id = NON_EXISTING_INCOME_ID;

    Assertions.assertThatExceptionOfType(NoResultException.class)
        .isThrownBy(() -> incomeServiceImpl.readByIdAndUser(id))
        .withMessage(String.format("Nenhuma receita com id = %d para esse usuário", NON_EXISTING_INCOME_ID));
  }

  @Test
  @DisplayName("readByYearAndMonthAndUser must returns Page<IncomeDto> when found successful")
  void readByYearAndMonthAndUser_ReturnsPageIncomeDto_WhenFoundSuccessful(){
    int month = 1;
    int year = 2022;

    Page<IncomeResponse> pageIncomeDto = incomeServiceImpl.readByYearAndMonthAndUser(year, month, PAGEABLE);

    Assertions.assertThat(pageIncomeDto).isNotEmpty();
    Assertions.assertThat(pageIncomeDto.getNumberOfElements()).isEqualTo(1);
  }

  @Test
  @DisplayName("readByYearAndMonthAndUser must throws NoResultException when don't has incomes")
  void readByYearAndMonthAndUser_ThrowsNoResultException_WhenDontHasIncomes(){
    int month = 1;
    int year = 2000;

    Assertions.assertThatExceptionOfType(NoResultException.class)
        .isThrownBy(() -> incomeServiceImpl.readByYearAndMonthAndUser(year, month, PAGEABLE))
        .withMessage(String.format("Não há receitas para o ano %d e mês %d", year, month));
  }

  @Test
  @DisplayName("readByYearAndMonthAndUser must returns empty page when user has incomes but request a page without data")
  void readByYearAndMonthAndUser_ReturnsEmptyPage_WhenUserHasIncomesButRequestAPageWithoutData(){
    int year = 2023;
    int month = 9;
    Page<IncomeResponse> pageIncomeDto = incomeServiceImpl.readByYearAndMonthAndUser(year, month, PAGEABLE_WITH_PAGE_ONE);

    Assertions.assertThat(pageIncomeDto).isEmpty();
    Assertions.assertThat(pageIncomeDto.getTotalElements()).isEqualTo(4L);
  }

  @Test
  @DisplayName("update must returns IncomeDto updated when successful")
  void update_ReturnsIncomeDtoUpdated_WhenSuccessful() {
    Long id = IncomeCreator.incomeWithAllArgs().getId();
    IncomeRequest incomeRequest = IncomeFormCreator.validIncomeForm();

    IncomeResponse updateIncome = incomeServiceImpl.update(id, incomeRequest);

    Assertions.assertThat(updateIncome).isNotNull();
    Assertions.assertThat(updateIncome.getId()).isEqualTo(id);
    Assertions.assertThat(updateIncome.getDescription()).isEqualTo(incomeRequest.getDescription());
  }

  @Test
  @DisplayName("update must throws ResponseStatusException when user already has another income with same description")
  void update_MustThrowsResponseStatusException_WhenUserHasAnotherIncomeWithSameDescription() {
    BDDMockito.when(incomeRepositoryMock.existsByDescriptionAndMonthAndYearAndNotIdAndUser(
        INCOME_FORM.getDescription(),
        INCOME_FORM.parseDateToLocalDate().getMonthValue(),
        INCOME_FORM.parseDateToLocalDate().getYear(),
        1000L)).thenReturn(true);

    Long id = IncomeCreator.incomeWithAllArgs().getId();
    IncomeRequest incomeRequest = IncomeFormCreator.validIncomeForm();

    Assertions.assertThatExceptionOfType(ResponseStatusException.class)
        .isThrownBy(() -> incomeServiceImpl.update(id, incomeRequest))
        .withMessageContaining("Outra receita com essa descrição já existe em ");
  }

  @Test
  @DisplayName("update must throws NoResultException when income don't exists")
  void update_ThrowsNoResultException_WhenIncomeDontExists() {
    IncomeRequest incomeRequest = IncomeFormCreator.validIncomeForm();

    Assertions.assertThatExceptionOfType(NoResultException.class)
        .isThrownBy(() -> incomeServiceImpl.update(NON_EXISTING_INCOME_ID, incomeRequest))
        .withMessage(String.format("Nenhuma receita com id = %d para esse usuário", NON_EXISTING_INCOME_ID));
  }

  @Test
  @DisplayName("readByYearAndMonthAndUser must throws NoResultException income don't exists")
  void deleteById_ThrowsNoResultException_WhenIncomeDontExists(){
    Assertions.assertThatExceptionOfType(NoResultException.class)
        .isThrownBy(() -> incomeServiceImpl.deleteById(NON_EXISTING_INCOME_ID))
        .withMessage(String.format("Nenhuma receita com id = %d para esse usuário", NON_EXISTING_INCOME_ID));
  }

}
