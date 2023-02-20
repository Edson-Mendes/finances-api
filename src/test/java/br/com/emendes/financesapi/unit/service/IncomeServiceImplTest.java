package br.com.emendes.financesapi.unit.service;

import br.com.emendes.financesapi.dto.request.IncomeRequest;
import br.com.emendes.financesapi.dto.response.IncomeResponse;
import br.com.emendes.financesapi.model.entity.Income;
import br.com.emendes.financesapi.repository.IncomeRepository;
import br.com.emendes.financesapi.service.impl.IncomeServiceImpl;
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
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.NoResultException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static br.com.emendes.financesapi.util.constant.ConstantForTesting.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests for IncomeServiceImpl")
class IncomeServiceImplTest {

  @InjectMocks
  private IncomeServiceImpl incomeServiceImpl;

  @Mock
  private IncomeRepository incomeRepositoryMock;
  @Mock
  private AuthenticationFacade authenticationFacadeMock;

  @Nested
  @DisplayName("Tests for create method")
  class CreateMethod {

    @BeforeEach
    void setUp() {
      BDDMockito.when(authenticationFacadeMock.getAuthentication())
          .thenReturn(new TestingAuthenticationToken(USER, null));
    }

    @Test
    @DisplayName("create must returns IncomeResponse when create successfully")
    void create_MustReturnsIncomeResponse_WhenCreateSuccessfully() {
      BDDMockito.when(incomeRepositoryMock.save(any(Income.class)))
          .thenReturn(INCOME);

      IncomeRequest incomeRequest = IncomeRequest.builder()
          .description("Salário")
          .value(new BigDecimal("2500.00"))
          .date("2023-02-08")
          .build();

      IncomeResponse actualIncomeResponse = incomeServiceImpl.create(incomeRequest);

      Assertions.assertThat(actualIncomeResponse).isNotNull();
      Assertions.assertThat(actualIncomeResponse.getDescription()).isEqualTo("Salário");
      Assertions.assertThat(actualIncomeResponse.getValue()).isEqualTo(new BigDecimal("2500.00"));
      Assertions.assertThat(actualIncomeResponse.getDate()).isEqualTo("2023-02-08");
    }

  }

  @Nested
  @DisplayName("Tests for readAllByUser method")
  class ReadAllByUserMethod {

    @Test
    @DisplayName("readAllByUser must returns Page<IncomeResponse> when read all by user successfully")
    void readAllByUser_MustReturnsPageIncomeResponse_WhenReadAllByUserSuccessfully() {
      BDDMockito.when(incomeRepositoryMock.findAllByUser(any()))
          .thenReturn(new PageImpl<>(List.of(INCOME)));

      Page<IncomeResponse> actualIncomeResponsePage = incomeServiceImpl.readAllByUser(PAGEABLE);
      List<IncomeResponse> actualContent = actualIncomeResponsePage.getContent();

      Assertions.assertThat(actualIncomeResponsePage).isNotEmpty();
      Assertions.assertThat(actualIncomeResponsePage.getNumberOfElements()).isEqualTo(1);
      Assertions.assertThat(actualContent.get(0).getDescription()).isEqualTo("Salário");
      Assertions.assertThat(actualContent.get(0).getValue()).isEqualTo("2500.00");
    }

    @Test
    @DisplayName("readAllByUser must throws NoResultException when user has no incomes")
    void readAllByUser_ThrowsNoResultException_WhenUserHasNoIncomes() {
      BDDMockito.when(incomeRepositoryMock.findAllByUser(PAGEABLE))
          .thenReturn(Page.empty(PAGEABLE));

      Assertions.assertThatExceptionOfType(NoResultException.class)
          .isThrownBy(() -> incomeServiceImpl.readAllByUser(PAGEABLE))
          .withMessage("The user has no incomes");
    }

    @Test
    @DisplayName("readAllByUser must returns empty page when user has incomes but request a page without data")
    void readAllByUser_ReturnsEmptyPage_WhenUserHasIncomesButRequestAPageWithoutData() {
      BDDMockito.when(incomeRepositoryMock.findAllByUser(any()))
          .thenReturn(new PageImpl<>(Collections.emptyList(), PAGEABLE_WITH_PAGE_ONE, 4));

      Page<IncomeResponse> actualIncomeResponsePage = incomeServiceImpl.readAllByUser(PAGEABLE_WITH_PAGE_ONE);

      Assertions.assertThat(actualIncomeResponsePage).isEmpty();
      Assertions.assertThat(actualIncomeResponsePage.getTotalElements()).isEqualTo(4L);
    }

  }

  @Nested
  @DisplayName("Tests for readByDescriptionAndUser method")
  class ReadByDescriptionAndUserMethod {

    @Test
    @DisplayName("readByDescriptionAndUser must returns Page<IncomeResponse> when read successfully")
    void readByDescriptionAndUser_MustReturnsPageIncomeResponse_WhenReadSuccessfully() {
      BDDMockito.when(incomeRepositoryMock.findByDescriptionAndUser(eq("Salário"), any()))
          .thenReturn(new PageImpl<>(List.of(INCOME)));

      Page<IncomeResponse> actualIncomeResponsePage = incomeServiceImpl
          .readByDescriptionAndUser("Salário", PAGEABLE);
      List<IncomeResponse> actualContent = actualIncomeResponsePage.getContent();

      Assertions.assertThat(actualIncomeResponsePage).isNotEmpty();
      Assertions.assertThat(actualIncomeResponsePage.getNumberOfElements()).isEqualTo(1);
      Assertions.assertThat(actualContent.get(0).getDescription()).isEqualTo("Salário");
      Assertions.assertThat(actualContent.get(0).getValue()).isEqualTo("2500.00");
    }

    @Test
    @DisplayName("readByDescriptionAndUser must throws NoResultException when user has no incomes")
    void readByDescriptionAndUser_ThrowsNoResultException_WhenUserHasNoIncomes() {
      BDDMockito.when(incomeRepositoryMock.findByDescriptionAndUser(eq("Freela"), any()))
          .thenReturn(Page.empty(PAGEABLE));

      Assertions.assertThatExceptionOfType(NoResultException.class)
          .isThrownBy(() -> incomeServiceImpl.readByDescriptionAndUser("Freela", PAGEABLE))
          .withMessageContaining("The user has no incomes with a description similar to ");
    }

    @Test
    @DisplayName("readByDescriptionAndUser must returns empty page when user has incomes but request a page without data")
    void readByDescriptionAndUser_ReturnsEmptyPage_WhenUserHasIncomesButRequestAPageWithoutData() {
      BDDMockito.when(incomeRepositoryMock.findByDescriptionAndUser(eq("uber"), any()))
          .thenReturn(new PageImpl<>(Collections.emptyList(), PAGEABLE_WITH_PAGE_ONE, 4));

      Page<IncomeResponse> actualIncomeResponsePage = incomeServiceImpl
          .readByDescriptionAndUser("uber", PAGEABLE_WITH_PAGE_ONE);

      Assertions.assertThat(actualIncomeResponsePage).isEmpty();
      Assertions.assertThat(actualIncomeResponsePage.getTotalElements()).isEqualTo(4L);
    }

  }

  @Nested
  @DisplayName("Tests for readByIdAndUser method")
  class ReadByIdAndUserMethod {

    @Test
    @DisplayName("readByIdAndUser must returns incomeResponse when read by id and user successfully")
    void readByIdAndUser_MustReturnsIncomeResponse_WhenReadByIdAndUserSuccessfully() {
      BDDMockito.when(incomeRepositoryMock.findByIdAndUser(100_000L))
          .thenReturn(Optional.of(INCOME));

      IncomeResponse actualIncomeResponse = incomeServiceImpl.readByIdAndUser(100_000L);

      Assertions.assertThat(actualIncomeResponse).isNotNull();
      Assertions.assertThat(actualIncomeResponse.getId()).isEqualTo(100_000L);
      Assertions.assertThat(actualIncomeResponse.getDescription()).isEqualTo("Salário");
    }

    @Test
    @DisplayName("readByIdAndUser must throws NoResultException when income with id 999_999 no exists")
    void readByIdAndUser_MustThrowsNoResultException_WhenIncomeWithId999999NoExists() {
      BDDMockito.when(incomeRepositoryMock.findByIdAndUser(999_999L))
          .thenReturn(Optional.empty());

      Assertions.assertThatExceptionOfType(NoResultException.class)
          .isThrownBy(() -> incomeServiceImpl.readByIdAndUser(999_999L))
          .withMessage("Income not found");
    }

  }

  @Nested
  @DisplayName("Tests for readByYearAndMonthAndUser method")
  class ReadByYearAndMonthAndUserMethod {

    @Test
    @DisplayName("readByYearAndMonthAndUser must returns Page<IncomeResponse> when found successfully")
    void readByYearAndMonthAndUser_MustReturnsPageIncomeResponse_WhenFoundSuccessfully() {
      BDDMockito.when(incomeRepositoryMock.findByYearAndMonthAndUser(2023, 2, PAGEABLE))
          .thenReturn(new PageImpl<>(List.of(INCOME)));

      Page<IncomeResponse> actualIncomeResponsePage = incomeServiceImpl
          .readByYearAndMonthAndUser(2023, 2, PAGEABLE);
      List<IncomeResponse> actualContent = actualIncomeResponsePage.getContent();

      Assertions.assertThat(actualIncomeResponsePage).isNotEmpty();
      Assertions.assertThat(actualIncomeResponsePage.getNumberOfElements()).isEqualTo(1);
      Assertions.assertThat(actualContent.get(0).getDate().getYear()).isEqualTo(2023);
      Assertions.assertThat(actualContent.get(0).getDate().getMonthValue()).isEqualTo(2);
    }

    @Test
    @DisplayName("readByYearAndMonthAndUser must throws NoResultException when has no incomes")
    void readByYearAndMonthAndUser_MustThrowsNoResultException_WhenHasNoIncomes() {
      BDDMockito.when(incomeRepositoryMock.findByYearAndMonthAndUser(2023, 3, PAGEABLE))
          .thenReturn(Page.empty());

      Assertions.assertThatExceptionOfType(NoResultException.class)
          .isThrownBy(() -> incomeServiceImpl.readByYearAndMonthAndUser(2023, 3, PAGEABLE))
          .withMessage("Has no incomes for year 2023 and month MARCH");
    }

    @Test
    @DisplayName("readByYearAndMonthAndUser must returns empty page when user has incomes but request a page without data")
    void readByYearAndMonthAndUser_MustReturnsEmptyPage_WhenUserHasIncomesButRequestAPageWithoutData() {
      BDDMockito.when(incomeRepositoryMock.findByYearAndMonthAndUser(2023, 2, PAGEABLE_WITH_PAGE_ONE))
          .thenReturn(new PageImpl<>(Collections.emptyList(), PAGEABLE_WITH_PAGE_ONE, 4));

      Page<IncomeResponse> actualIncomeResponsePage = incomeServiceImpl
          .readByYearAndMonthAndUser(2023, 2, PAGEABLE_WITH_PAGE_ONE);

      Assertions.assertThat(actualIncomeResponsePage).isEmpty();
      Assertions.assertThat(actualIncomeResponsePage.getTotalElements()).isEqualTo(4L);
    }

  }

  @Nested
  @DisplayName("Tests for update method")
  class UpdateMethod {

    @Test
    @DisplayName("update must returns IncomeResponse when update successfully")
    void update_MustReturnsIncomeResponse_WhenUpdateSuccessfully() {
      Income income = Income.builder()
          .id(100_000L)
          .description("Salário")
          .value(new BigDecimal("2500.00"))
          .date(LocalDate.parse("2023-02-05"))
          .user(USER)
          .build();

      BDDMockito.when(incomeRepositoryMock.findByIdAndUser(100_000L))
          .thenReturn(Optional.of(income));

      IncomeRequest incomeRequest = IncomeRequest.builder()
          .description("Salário updated")
          .value(new BigDecimal("2750.00"))
          .date("2023-02-05")
          .build();

      IncomeResponse actualIncomeResponse = incomeServiceImpl.update(100_000L, incomeRequest);

      Assertions.assertThat(actualIncomeResponse).isNotNull();
      Assertions.assertThat(actualIncomeResponse.getId()).isEqualTo(100_000L);
      Assertions.assertThat(actualIncomeResponse.getDescription()).isEqualTo("Salário updated");
      Assertions.assertThat(actualIncomeResponse.getValue()).isEqualTo("2750.00");
    }

    @Test
    @DisplayName("update must throws NoResultException when income no exists")
    void update_MustThrowsNoResultException_WhenIncomeNoExists() {
      BDDMockito.when(incomeRepositoryMock.findByIdAndUser(999_999L))
          .thenReturn(Optional.empty());


      IncomeRequest incomeRequest = IncomeRequest.builder()
          .description("Salário updated")
          .value(new BigDecimal("2750.00"))
          .date("2023-02-05")
          .build();

      Assertions.assertThatExceptionOfType(NoResultException.class)
          .isThrownBy(() -> incomeServiceImpl.update(999_999L, incomeRequest))
          .withMessage("Income not found");
    }

  }

  @Nested
  @DisplayName("Tests for deleteById method")
  class DeleteByIdMethod {

    @Test
    @DisplayName("deleteById must call IncomeRepository#delete when delete successfully")
    void deleteById_MustCallIncomeRepositoryDelete_WhenDeleteSuccessfully() {
      BDDMockito.when(incomeRepositoryMock.findByIdAndUser(100_000L))
          .thenReturn(Optional.of(INCOME));

      incomeServiceImpl.deleteById(100_000L);

      BDDMockito.verify(incomeRepositoryMock).delete(any());
    }

    @Test
    @DisplayName("deleteById must throws NoResultException when no exists income with id 999_999")
    void deleteById_ThrowsNoResultException_WhenNoExistsIncomeWithId999999() {
      BDDMockito.when(incomeRepositoryMock.findByIdAndUser(999_999L))
          .thenReturn(Optional.empty());

      Assertions.assertThatExceptionOfType(NoResultException.class)
          .isThrownBy(() -> incomeServiceImpl.deleteById(999_999L))
          .withMessage("Income not found");
    }

  }

}
