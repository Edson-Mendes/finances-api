package br.com.emendes.financesapi.unit.service;

import br.com.emendes.financesapi.dto.request.IncomeRequest;
import br.com.emendes.financesapi.dto.response.IncomeResponse;
import br.com.emendes.financesapi.exception.EntityNotFoundException;
import br.com.emendes.financesapi.exception.UserIsNotAuthenticatedException;
import br.com.emendes.financesapi.model.entity.Income;
import br.com.emendes.financesapi.repository.IncomeRepository;
import br.com.emendes.financesapi.service.impl.IncomeServiceImpl;
import br.com.emendes.financesapi.util.component.CurrentAuthenticationComponent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
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
import static br.com.emendes.financesapi.util.faker.IncomeFaker.*;
import static br.com.emendes.financesapi.util.faker.UserFaker.user;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * IncomeServiceImpl unit tests.
 */
@ExtendWith(SpringExtension.class)
@DisplayName("Tests for IncomeServiceImpl")
class IncomeServiceImplTest {

  @InjectMocks
  private IncomeServiceImpl incomeServiceImpl;

  @Mock
  private IncomeRepository incomeRepositoryMock;
  @Mock
  private CurrentAuthenticationComponent currentAuthenticationComponentMock;

  @Nested
  @DisplayName("Tests for create method")
  class CreateMethod {

    @Test
    @DisplayName("create must returns IncomeResponse when create successfully")
    void create_MustReturnsIncomeResponse_WhenCreateSuccessfully() {
      when(currentAuthenticationComponentMock.getCurrentUser()).thenReturn(user());
      when(incomeRepositoryMock.save(any(Income.class)))
          .thenReturn(income());

      IncomeRequest incomeRequest = IncomeRequest.builder()
          .description("Salário")
          .value(new BigDecimal("2500.00"))
          .date("2023-02-08")
          .build();

      IncomeResponse actualIncomeResponse = incomeServiceImpl.create(incomeRequest);

      assertThat(actualIncomeResponse).isNotNull();
      assertThat(actualIncomeResponse.getDescription()).isEqualTo("Salário");
      assertThat(actualIncomeResponse.getValue()).isEqualTo(new BigDecimal("2500.00"));
      assertThat(actualIncomeResponse.getDate()).isEqualTo("2023-02-08");
    }

    @Test
    @DisplayName("create must throws UserIsNotAuthenticatedException when user is not authenticated")
    void create_MustThrowsUserIsNotAuthenticatedException_WhenUserIsNotAuthenticated() {
      when(currentAuthenticationComponentMock.getCurrentUser())
          .thenThrow(new UserIsNotAuthenticatedException("User is not authenticate"));

      IncomeRequest incomeRequest = IncomeRequest.builder()
          .description("Salário")
          .value(new BigDecimal("2500.00"))
          .date("2023-02-08")
          .build();

      assertThatExceptionOfType(UserIsNotAuthenticatedException.class)
          .isThrownBy(() -> incomeServiceImpl.create(incomeRequest))
          .withMessage("User is not authenticate");
    }

  }

  @Nested
  @DisplayName("Tests for readAllByUser method")
  class ReadAllByUserMethod {

    @Test
    @DisplayName("readAllByUser must returns Page<IncomeResponse> when read all by user successfully")
    void readAllByUser_MustReturnsPageIncomeResponse_WhenReadAllByUserSuccessfully() {
      when(currentAuthenticationComponentMock.getCurrentUser()).thenReturn(user());
      when(incomeRepositoryMock.findAllByUser(any(), any()))
          .thenReturn(new PageImpl<>(incomeList()));

      Page<IncomeResponse> actualIncomeResponsePage = incomeServiceImpl.readAllByUser(PAGEABLE);
      List<IncomeResponse> actualContent = actualIncomeResponsePage.getContent();

      assertThat(actualIncomeResponsePage).isNotEmpty();
      assertThat(actualIncomeResponsePage.getNumberOfElements()).isEqualTo(1);
      assertThat(actualContent.get(0).getDescription()).isEqualTo("Salário");
      assertThat(actualContent.get(0).getValue()).isEqualTo("2500.00");
    }

    @Test
    @DisplayName("readAllByUser must throws EntityNotFoundException when user has no incomes")
    void readAllByUser_ThrowsEntityNotFoundException_WhenUserHasNoIncomes() {
      when(currentAuthenticationComponentMock.getCurrentUser()).thenReturn(user());
      when(incomeRepositoryMock.findAllByUser(any(), any()))
          .thenReturn(Page.empty(PAGEABLE));

      assertThatExceptionOfType(EntityNotFoundException.class)
          .isThrownBy(() -> incomeServiceImpl.readAllByUser(PAGEABLE))
          .withMessage("The user has no incomes");
    }

    @Test
    @DisplayName("readAllByUser must returns empty page when user has incomes but request a page without data")
    void readAllByUser_ReturnsEmptyPage_WhenUserHasIncomesButRequestAPageWithoutData() {
      when(currentAuthenticationComponentMock.getCurrentUser()).thenReturn(user());
      when(incomeRepositoryMock.findAllByUser(any(), any()))
          .thenReturn(new PageImpl<>(Collections.emptyList(), PAGEABLE_WITH_PAGE_ONE, 4));

      Page<IncomeResponse> actualIncomeResponsePage = incomeServiceImpl.readAllByUser(PAGEABLE_WITH_PAGE_ONE);

      assertThat(actualIncomeResponsePage).isEmpty();
      assertThat(actualIncomeResponsePage.getTotalElements()).isEqualTo(4L);
    }

    @Test
    @DisplayName("readAllByUser must throws UserIsNotAuthenticatedException when user is not authenticated")
    void readAllByUser_ThrowsUserIsNotAuthenticatedException_WhenUserIsNotAuthenticated() {
      when(currentAuthenticationComponentMock.getCurrentUser())
          .thenThrow(new UserIsNotAuthenticatedException("User is not authenticate"));

      assertThatExceptionOfType(UserIsNotAuthenticatedException.class)
          .isThrownBy(() -> incomeServiceImpl.readAllByUser(PAGEABLE))
          .withMessage("User is not authenticate");
    }

  }

  @Nested
  @DisplayName("Tests for readByDescriptionAndUser method")
  class ReadByDescriptionAndUserMethod {

    @Test
    @DisplayName("readByDescriptionAndUser must returns Page<IncomeResponse> when read successfully")
    void readByDescriptionAndUser_MustReturnsPageIncomeResponse_WhenReadSuccessfully() {
      when(currentAuthenticationComponentMock.getCurrentUser()).thenReturn(user());
      when(incomeRepositoryMock.findByDescriptionAndUser(eq("Salário"), any(), any()))
          .thenReturn(new PageImpl<>(incomeList(), PAGEABLE, 1));

      Page<IncomeResponse> actualIncomeResponsePage = incomeServiceImpl
          .readByDescriptionAndUser("Salário", PAGEABLE);
      List<IncomeResponse> actualContent = actualIncomeResponsePage.getContent();

      assertThat(actualIncomeResponsePage).isNotEmpty();
      assertThat(actualIncomeResponsePage.getNumberOfElements()).isEqualTo(1);
      assertThat(actualContent.get(0).getDescription()).isEqualTo("Salário");
      assertThat(actualContent.get(0).getValue()).isEqualTo("2500.00");
    }

    @Test
    @DisplayName("readByDescriptionAndUser must returns empty page when user has incomes but request a page without data")
    void readByDescriptionAndUser_ReturnsEmptyPage_WhenUserHasIncomesButRequestAPageWithoutData() {
      when(currentAuthenticationComponentMock.getCurrentUser()).thenReturn(user());
      when(incomeRepositoryMock.findByDescriptionAndUser(eq("uber"), any(), any()))
          .thenReturn(new PageImpl<>(Collections.emptyList(), PAGEABLE_WITH_PAGE_ONE, 4));

      Page<IncomeResponse> actualIncomeResponsePage = incomeServiceImpl
          .readByDescriptionAndUser("uber", PAGEABLE_WITH_PAGE_ONE);

      assertThat(actualIncomeResponsePage).isEmpty();
      assertThat(actualIncomeResponsePage.getTotalElements()).isEqualTo(4L);
    }

    @Test
    @DisplayName("readByDescriptionAndUser must throws EntityNotFoundException when user has no incomes")
    void readByDescriptionAndUser_ThrowsEntityNotFoundException_WhenUserHasNoIncomes() {
      when(currentAuthenticationComponentMock.getCurrentUser()).thenReturn(user());
      when(incomeRepositoryMock.findByDescriptionAndUser(eq("Freela"), any(), any()))
          .thenReturn(Page.empty(PAGEABLE));

      assertThatExceptionOfType(EntityNotFoundException.class)
          .isThrownBy(() -> incomeServiceImpl.readByDescriptionAndUser("Freela", PAGEABLE))
          .withMessageContaining("The user has no incomes with a description similar to ");
    }

    @Test
    @DisplayName("readByDescriptionAndUser must throws UserIsNotAuthenticatedException when user is not authenticated")
    void readByDescriptionAndUser_ThrowsUserIsNotAuthenticatedException_WhenUserIsNotAuthenticated() {
      when(currentAuthenticationComponentMock.getCurrentUser())
          .thenThrow(new UserIsNotAuthenticatedException("User is not authenticate"));

      assertThatExceptionOfType(UserIsNotAuthenticatedException.class)
          .isThrownBy(() -> incomeServiceImpl.readByDescriptionAndUser("Freela", PAGEABLE))
          .withMessage("User is not authenticate");
    }

  }

  @Nested
  @DisplayName("Tests for readByIdAndUser method")
  class ReadByIdAndUserMethod {

    @Test
    @DisplayName("readByIdAndUser must returns incomeResponse when read by id and user successfully")
    void readByIdAndUser_MustReturnsIncomeResponse_WhenReadByIdAndUserSuccessfully() {
      when(currentAuthenticationComponentMock.getCurrentUser()).thenReturn(user());
      when(incomeRepositoryMock.findByIdAndUser(eq(100_000L), any()))
          .thenReturn(incomeOptional());

      IncomeResponse actualIncomeResponse = incomeServiceImpl.readByIdAndUser(100_000L);

      assertThat(actualIncomeResponse).isNotNull();
      assertThat(actualIncomeResponse.getId()).isEqualTo(100_000L);
      assertThat(actualIncomeResponse.getDescription()).isEqualTo("Salário");
    }

    @Test
    @DisplayName("readByIdAndUser must throws EntityNotFoundException when income with id 999_999 no exists")
    void readByIdAndUser_MustThrowsEntityNotFoundException_WhenIncomeWithId999999NoExists() {
      when(currentAuthenticationComponentMock.getCurrentUser()).thenReturn(user());
      when(incomeRepositoryMock.findByIdAndUser(eq(999_999L), any()))
          .thenReturn(Optional.empty());

      assertThatExceptionOfType(EntityNotFoundException.class)
          .isThrownBy(() -> incomeServiceImpl.readByIdAndUser(999_999L))
          .withMessage("Income not found with id: 999999");
    }

    @Test
    @DisplayName("readByIdAndUser must throws UserIsNotAuthenticatedException when user is not authenticated")
    void readByIdAndUser_ThrowsUserIsNotAuthenticatedException_WhenUserIsNotAuthenticated() {
      when(currentAuthenticationComponentMock.getCurrentUser())
          .thenThrow(new UserIsNotAuthenticatedException("User is not authenticate"));

      assertThatExceptionOfType(UserIsNotAuthenticatedException.class)
          .isThrownBy(() -> incomeServiceImpl.readByIdAndUser(100_000L))
          .withMessage("User is not authenticate");
    }

  }

  @Nested
  @DisplayName("Tests for readByYearAndMonthAndUser method")
  class ReadByYearAndMonthAndUserMethod {

    @Test
    @DisplayName("readByYearAndMonthAndUser must returns Page<IncomeResponse> when found successfully")
    void readByYearAndMonthAndUser_MustReturnsPageIncomeResponse_WhenFoundSuccessfully() {
      when(currentAuthenticationComponentMock.getCurrentUser()).thenReturn(user());
      when(incomeRepositoryMock.findByYearAndMonthAndUser(eq(2023), eq(2), any(), eq(PAGEABLE)))
          .thenReturn(new PageImpl<>(incomeList(), PAGEABLE, 1));

      Page<IncomeResponse> actualIncomeResponsePage = incomeServiceImpl
          .readByYearAndMonthAndUser(2023, 2, PAGEABLE);
      List<IncomeResponse> actualContent = actualIncomeResponsePage.getContent();

      assertThat(actualIncomeResponsePage).isNotEmpty();
      assertThat(actualIncomeResponsePage.getNumberOfElements()).isEqualTo(1);
      assertThat(actualContent.get(0).getDate().getYear()).isEqualTo(2023);
      assertThat(actualContent.get(0).getDate().getMonthValue()).isEqualTo(2);
    }

    @Test
    @DisplayName("readByYearAndMonthAndUser must returns empty page when user has incomes but request a page without data")
    void readByYearAndMonthAndUser_MustReturnsEmptyPage_WhenUserHasIncomesButRequestAPageWithoutData() {
      when(currentAuthenticationComponentMock.getCurrentUser()).thenReturn(user());
      when(incomeRepositoryMock.findByYearAndMonthAndUser(eq(2023), eq(2), any(), eq(PAGEABLE_WITH_PAGE_ONE)))
          .thenReturn(new PageImpl<>(Collections.emptyList(), PAGEABLE_WITH_PAGE_ONE, 4));

      Page<IncomeResponse> actualIncomeResponsePage = incomeServiceImpl
          .readByYearAndMonthAndUser(2023, 2, PAGEABLE_WITH_PAGE_ONE);

      assertThat(actualIncomeResponsePage).isEmpty();
      assertThat(actualIncomeResponsePage.getTotalElements()).isEqualTo(4L);
    }

    @Test
    @DisplayName("readByYearAndMonthAndUser must throws EntityNotFoundException when has no incomes")
    void readByYearAndMonthAndUser_MustThrowsEntityNotFoundException_WhenHasNoIncomes() {
      when(currentAuthenticationComponentMock.getCurrentUser()).thenReturn(user());
      when(incomeRepositoryMock.findByYearAndMonthAndUser(eq(2023), eq(3), any(), eq(PAGEABLE)))
          .thenReturn(Page.empty());

      assertThatExceptionOfType(EntityNotFoundException.class)
          .isThrownBy(() -> incomeServiceImpl.readByYearAndMonthAndUser(2023, 3, PAGEABLE))
          .withMessage("Has no incomes for year 2023 and month MARCH");
    }

    @Test
    @DisplayName("readByYearAndMonthAndUser must throws UserIsNotAuthenticatedException when user is not authenticated")
    void readByYearAndMonthAndUser_ThrowsUserIsNotAuthenticatedException_WhenUserIsNotAuthenticated() {
      when(currentAuthenticationComponentMock.getCurrentUser())
          .thenThrow(new UserIsNotAuthenticatedException("User is not authenticate"));

      assertThatExceptionOfType(UserIsNotAuthenticatedException.class)
          .isThrownBy(() -> incomeServiceImpl.readByYearAndMonthAndUser(2023, 3, PAGEABLE))
          .withMessage("User is not authenticate");
    }

  }

  @Nested
  @DisplayName("Tests for update method")
  class UpdateMethod {

    @Test
    @DisplayName("update must returns IncomeResponse when update successfully")
    void update_MustReturnsIncomeResponse_WhenUpdateSuccessfully() {
      when(currentAuthenticationComponentMock.getCurrentUser()).thenReturn(user());
      when(incomeRepositoryMock.findByIdAndUser(eq(100_000L), any()))
          .thenReturn(incomeOptional());

      IncomeRequest incomeRequest = IncomeRequest.builder()
          .description("Salário updated")
          .value(new BigDecimal("2750.00"))
          .date("2023-02-05")
          .build();

      IncomeResponse actualIncomeResponse = incomeServiceImpl.update(100_000L, incomeRequest);

      assertThat(actualIncomeResponse).isNotNull();
      assertThat(actualIncomeResponse.getId()).isEqualTo(100_000L);
      assertThat(actualIncomeResponse.getDescription()).isEqualTo("Salário updated");
      assertThat(actualIncomeResponse.getValue()).isEqualTo("2750.00");
    }

    @Test
    @DisplayName("update must throws EntityNotFoundException when income no exists")
    void update_MustThrowsEntityNotFoundException_WhenIncomeNoExists() {
      when(currentAuthenticationComponentMock.getCurrentUser()).thenReturn(user());
      when(incomeRepositoryMock.findByIdAndUser(eq(999_999L), any()))
          .thenReturn(Optional.empty());


      IncomeRequest incomeRequest = IncomeRequest.builder()
          .description("Salário updated")
          .value(new BigDecimal("2750.00"))
          .date("2023-02-05")
          .build();

      assertThatExceptionOfType(EntityNotFoundException.class)
          .isThrownBy(() -> incomeServiceImpl.update(999_999L, incomeRequest))
          .withMessage("Income not found with id: 999999");
    }

    @Test
    @DisplayName("update must throws UserIsNotAuthenticatedException when user is not authenticated")
    void update_ThrowsUserIsNotAuthenticatedException_WhenUserIsNotAuthenticated() {
      when(currentAuthenticationComponentMock.getCurrentUser())
          .thenThrow(new UserIsNotAuthenticatedException("User is not authenticate"));

      IncomeRequest incomeRequest = IncomeRequest.builder()
          .description("Salário updated")
          .value(new BigDecimal("2750.00"))
          .date("2023-02-05")
          .build();

      assertThatExceptionOfType(UserIsNotAuthenticatedException.class)
          .isThrownBy(() -> incomeServiceImpl.update(100_000L, incomeRequest))
          .withMessage("User is not authenticate");
    }

  }

  @Nested
  @DisplayName("Tests for deleteById method")
  class DeleteByIdMethod {

    @Test
    @DisplayName("deleteById must call IncomeRepository#delete when delete successfully")
    void deleteById_MustCallIncomeRepositoryDelete_WhenDeleteSuccessfully() {
      when(currentAuthenticationComponentMock.getCurrentUser()).thenReturn(user());
      when(incomeRepositoryMock.findByIdAndUser(eq(100_000L), any()))
          .thenReturn(incomeOptional());

      incomeServiceImpl.deleteById(100_000L);

      BDDMockito.verify(incomeRepositoryMock).delete(any());
    }

    @Test
    @DisplayName("deleteById must throws EntityNotFoundException when no exists income with id 999_999")
    void deleteById_ThrowsEntityNotFoundException_WhenNoExistsIncomeWithId999999() {
      when(currentAuthenticationComponentMock.getCurrentUser()).thenReturn(user());
      when(incomeRepositoryMock.findByIdAndUser(eq(999_999L), any()))
          .thenReturn(Optional.empty());

      assertThatExceptionOfType(EntityNotFoundException.class)
          .isThrownBy(() -> incomeServiceImpl.deleteById(999_999L))
          .withMessage("Income not found with id: 999999");
    }

    @Test
    @DisplayName("deleteById must throws UserIsNotAuthenticatedException when user is not authenticated")
    void deleteById_ThrowsUserIsNotAuthenticatedException_WhenUserIsNotAuthenticated() {
      when(currentAuthenticationComponentMock.getCurrentUser())
          .thenThrow(new UserIsNotAuthenticatedException("User is not authenticate"));

      assertThatExceptionOfType(UserIsNotAuthenticatedException.class)
          .isThrownBy(() -> incomeServiceImpl.deleteById(100_000L))
          .withMessage("User is not authenticate");
    }

  }

  @Nested
  @DisplayName("Tests for getTotalValueByMonthAndYearAndUser method")
  class GetTotalValueByMonthAndYearAndUserMethod {

    @Test
    @DisplayName("getTotalValueByMonthAndYearAndUser must return total value when get successfully")
    void getTotalValueByMonthAndYearAndUser_MustReturnTotalValue_WhenGetSuccessfully() {
      when(currentAuthenticationComponentMock.getCurrentUser()).thenReturn(user());
      when(incomeRepositoryMock.getTotalValueByMonthAndYearAndUser(anyInt(), anyInt(), any()))
          .thenReturn(Optional.of(new BigDecimal("2500.00")));

      BigDecimal actualTotalValue = incomeServiceImpl.getTotalValueByMonthAndYearAndUserId(2023, 2);

      assertThat(actualTotalValue).isNotNull().isEqualTo("2500.00");
    }

    @Test
    @DisplayName("getTotalValueByMonthAndYearAndUser must return zero when user has not incomes for given year and month")
    void getTotalValueByMonthAndYearAndUser_MustReturnTotalValue_WhenUserHasNotIncomesForGivenYearAndMonth() {
      when(currentAuthenticationComponentMock.getCurrentUser()).thenReturn(user());
      when(incomeRepositoryMock.getTotalValueByMonthAndYearAndUser(anyInt(), anyInt(), any()))
          .thenReturn(Optional.empty());

      BigDecimal actualTotalValue = incomeServiceImpl.getTotalValueByMonthAndYearAndUserId(2023, 2);

      assertThat(actualTotalValue).isNotNull().isZero();
    }

    @Test
    @DisplayName("getTotalValueByMonthAndYearAndUser must throws UserIsNotAuthenticatedException when user is not authenticated")
    void getTotalValueByMonthAndYearAndUser_ThrowsUserIsNotAuthenticatedException_WhenUserIsNotAuthenticated() {
      when(currentAuthenticationComponentMock.getCurrentUser())
          .thenThrow(new UserIsNotAuthenticatedException("User is not authenticate"));

      assertThatExceptionOfType(UserIsNotAuthenticatedException.class)
          .isThrownBy(() -> incomeServiceImpl.getTotalValueByMonthAndYearAndUserId(2023, 2))
          .withMessage("User is not authenticate");
    }

  }

}
