package br.com.emendes.financesapi.integration.repository;

import br.com.emendes.financesapi.model.entity.Income;
import br.com.emendes.financesapi.model.entity.User;
import br.com.emendes.financesapi.repository.IncomeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.jdbc.SqlMergeMode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static br.com.emendes.financesapi.util.constant.ConstantForTesting.PAGEABLE;
import static br.com.emendes.financesapi.util.constant.SqlPath.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests da camada income repository interagindo com o banco de dados.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("repository-it")
@DisplayName("Integration tests for IncomeRepository")
@SqlGroup({
    @Sql(scripts = {DROP_DATABASE_TABLES_SQL_PATH, CREATE_DATABASE_TABLES_SQL_PATH})
})
class IncomeRepositoryIT {

  @Autowired
  private IncomeRepository incomeRepository;

  @Nested
  @DisplayName("FindAllByUser method")
  class FindAllByUserMethod {

    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    @Sql(scripts = INSERT_MULTIPLE_INCOMES_AND_MULTIPLE_USERS)
    @Test
    @DisplayName("findAllByUser must return page with two incomes when found incomes for given User")
    void findAllByUser_MustReturnPageWithTwoIncomes_WhenFoundIncomesForGivenUser() {
      User user = User.builder().id(1L).build();

      Page<Income> actualIncomePage = incomeRepository.findAllByUser(user, PAGEABLE);

      assertThat(actualIncomePage).isNotNull().hasSize(2);
    }

    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    @Sql(scripts = INSERT_MULTIPLE_INCOMES_AND_MULTIPLE_USERS)
    @Test
    @DisplayName("findAllByUser must return empty page when not found incomes for given User")
    void findAllByUser_MustReturnEmptyPage_WhenNotFoundIncomesForGivenUser() {
      User user = User.builder().id(5L).build();

      Page<Income> actualIncomePage = incomeRepository.findAllByUser(user, PAGEABLE);

      assertThat(actualIncomePage).isNotNull().isEmpty();
    }

  }

  @Nested
  @DisplayName("FindByDescriptionAndUser method")
  class FindByDescriptionAndUserMethod {

    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    @Sql(scripts = INSERT_MULTIPLE_INCOMES_FOR_ONE_USER)
    @Test
    @DisplayName("findByDescriptionAndUser must return page with two incomes when found for given user and description 'sala'")
    void findByDescriptionAndUser_MustReturnPageWithTwoIncomes_WhenFoundForGivenUserAndDescriptionSala() {
      User user = User.builder().id(1L).build();

      Page<Income> actualIncomePage = incomeRepository.findByDescriptionAndUser("sala", user, PAGEABLE);
      assertThat(actualIncomePage).isNotNull().hasSize(2);

      Page<String> actualIncomesDescriptions = actualIncomePage.map(Income::getDescription);
      assertThat(actualIncomesDescriptions)
          .isNotNull().hasSize(2)
          .allMatch(description -> description.toLowerCase().contains("salá"));
    }

    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    @Sql(scripts = INSERT_MULTIPLE_INCOMES_AND_MULTIPLE_USERS)
    @Test
    @DisplayName("findByDescriptionAndUser must return page with one income when exists income with same description for different users")
    void findByDescriptionAndUser_MustReturnPageWithOneIncome_WhenExistsIncomeWithSameDescriptionForDifferentUsers() {
      User user = User.builder().id(1L).build();

      Page<Income> actualIncomePage = incomeRepository.findByDescriptionAndUser("sala", user, PAGEABLE);
      assertThat(actualIncomePage).isNotNull().hasSize(1);

      Page<String> actualIncomesDescriptions = actualIncomePage.map(Income::getDescription);
      assertThat(actualIncomesDescriptions)
          .isNotNull().hasSize(1)
          .allMatch(description -> description.toLowerCase().contains("salá"));
    }

    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    @Sql(scripts = INSERT_MULTIPLE_INCOMES_FOR_ONE_USER)
    @Test
    @DisplayName("findByDescriptionAndUser must return empty page when not found for given user and description 'venda brechó'")
    void findByDescriptionAndUser_MustReturnEmptyPage_WhenNotFoundForGivenUserAndDescriptionVendaBrecho() {
      User user = User.builder().id(1L).build();

      Page<Income> actualIncomePage = incomeRepository.findByDescriptionAndUser("venda brechó", user, PAGEABLE);
      assertThat(actualIncomePage).isNotNull().isEmpty();
    }

    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    @Sql(scripts = INSERT_MULTIPLE_INCOMES_AND_MULTIPLE_USERS)
    @Test
    @DisplayName("findByDescriptionAndUser must return empty page when not found for given user")
    void findByDescriptionAndUser_MustReturnEmptyPage_WhenNotFoundForGivenUser() {
      User user = User.builder().id(1L).build();

      Page<Income> actualIncomePage = incomeRepository.findByDescriptionAndUser("venda", user, PAGEABLE);
      assertThat(actualIncomePage).isNotNull().isEmpty();
    }

  }

  @Nested
  @DisplayName("FindByYearAndMonthAndUser method")
  class FindByYearAndMonthAndUserMethod {

    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    @Sql(scripts = INSERT_MULTIPLE_INCOMES_FOR_ONE_USER)
    @Test
    @DisplayName("findByYearAndMonthAndUser must return Page with three incomes when found for given user, year and month")
    void findByYearAndMonthAndUser_MustReturnPageWithThreeIncomes_WhenFoundForGivenUserAndYearAndMonth() {
      User user = User.builder().id(1L).build();

      Page<Income> actualIncomePage = incomeRepository.findByYearAndMonthAndUser(2023, 2, user, PAGEABLE);
      assertThat(actualIncomePage).isNotNull().hasSize(3);

      Page<LocalDate> actualIncomesDescriptions = actualIncomePage.map(Income::getDate);
      assertThat(actualIncomesDescriptions)
          .isNotNull().hasSize(3)
          .allMatch(date -> date.getYear() == 2023 && date.getMonthValue() == 2);
    }

    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    @Sql(scripts = INSERT_MULTIPLE_INCOMES_AND_MULTIPLE_USERS)
    @Test
    @DisplayName("findByYearAndMonthAndUser must return Page with two incomes when exists incomes with same year and month and different users")
    void findByYearAndMonthAndUser_MustReturnPageWithTwoIncomes_WhenExistsIncomeWithSameYearAndMonthAndDifferentUsers() {
      User user = User.builder().id(1L).build();

      Page<Income> actualIncomePage = incomeRepository.findByYearAndMonthAndUser(2023, 2, user, PAGEABLE);
      assertThat(actualIncomePage).isNotNull().hasSize(2);

      Page<LocalDate> actualIncomesDescriptions = actualIncomePage.map(Income::getDate);
      assertThat(actualIncomesDescriptions)
          .isNotNull().hasSize(2)
          .allMatch(date -> date.getYear() == 2023 && date.getMonthValue() == 2);
    }

    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    @Sql(scripts = INSERT_MULTIPLE_INCOMES_FOR_ONE_USER)
    @Test
    @DisplayName("findByYearAndMonthAndUser must return empty Page when not found for given user, year and month")
    void findByYearAndMonthAndUser_MustReturnEmptyPage_WhenNotFoundForGivenUserAndYearAndMonth() {
      User user = User.builder().id(2L).build();

      Page<Income> actualIncomePage = incomeRepository.findByYearAndMonthAndUser(2021, 11, user, PAGEABLE);
      assertThat(actualIncomePage).isNotNull().isEmpty();
    }

    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    @Sql(scripts = INSERT_MULTIPLE_INCOMES_FOR_ONE_USER)
    @Test
    @DisplayName("findByYearAndMonthAndUser must return empty Page when not found for given user")
    void findByYearAndMonthAndUser_MustReturnEmptyPage_WhenNotFoundForGivenUser() {
      User user = User.builder().id(2L).build();

      Page<Income> actualIncomePage = incomeRepository.findByYearAndMonthAndUser(2023, 3, user, PAGEABLE);
      assertThat(actualIncomePage).isNotNull().isEmpty();
    }

    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    @Sql(scripts = INSERT_MULTIPLE_INCOMES_FOR_ONE_USER)
    @Test
    @DisplayName("findByYearAndMonthAndUser must return empty Page when not found for given year")
    void findByYearAndMonthAndUser_MustReturnEmptyPage_WhenNotFoundForGivenYear() {
      User user = User.builder().id(1L).build();

      Page<Income> actualIncomePage = incomeRepository.findByYearAndMonthAndUser(2021, 3, user, PAGEABLE);
      assertThat(actualIncomePage).isNotNull().isEmpty();
    }

    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    @Sql(scripts = INSERT_MULTIPLE_INCOMES_FOR_ONE_USER)
    @Test
    @DisplayName("findByYearAndMonthAndUser must return empty Page when not found for given month")
    void findByYearAndMonthAndUser_MustReturnEmptyPage_WhenNotFoundForGivenMonth() {
      User user = User.builder().id(1L).build();

      Page<Income> actualIncomePage = incomeRepository.findByYearAndMonthAndUser(2023, 11, user, PAGEABLE);
      assertThat(actualIncomePage).isNotNull().isEmpty();
    }

  }

  @Nested
  @DisplayName("FindByIdAndUser method")
  class FindByIdAndUserMethod {

    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    @Sql(scripts = INSERT_MULTIPLE_INCOMES_FOR_ONE_USER)
    @Test
    @DisplayName("findByIdAndUser must return Optional<Income> when found for given id and user")
    void findByIdAndUser_MustReturnPageWithThreeIncomes_WhenFoundForGivenIdAndUser() {
      User user = User.builder().id(1L).build();

      Optional<Income> actualIncomeOptional = incomeRepository.findByIdAndUser(2L, user);
      assertThat(actualIncomeOptional).isNotNull().isNotEmpty();
    }

    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    @Sql(scripts = INSERT_MULTIPLE_INCOMES_FOR_ONE_USER)
    @Test
    @DisplayName("findByIdAndUser must return empty Optional when not found for given id and user")
    void findByIdAndUser_MustReturnEmptyOptional_WhenNotFoundForGivenIdAndUser() {
      User user = User.builder().id(2L).build();

      Optional<Income> actualIncomeOptional = incomeRepository.findByIdAndUser(100L, user);
      assertThat(actualIncomeOptional).isNotNull().isEmpty();
    }

    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    @Sql(scripts = INSERT_MULTIPLE_INCOMES_FOR_ONE_USER)
    @Test
    @DisplayName("findByIdAndUser must return empty Optional when not found for given id")
    void findByIdAndUser_MustReturnEmptyOptional_WhenNotFoundForGivenId() {
      User user = User.builder().id(1L).build();

      Optional<Income> actualIncomeOptional = incomeRepository.findByIdAndUser(100L, user);
      assertThat(actualIncomeOptional).isNotNull().isEmpty();
    }

    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    @Sql(scripts = INSERT_MULTIPLE_INCOMES_FOR_ONE_USER)
    @Test
    @DisplayName("findByIdAndUser must return empty Optional when not found for given user")
    void findByIdAndUser_MustReturnEmptyOptional_WhenNotFoundForGivenUser() {
      User user = User.builder().id(2L).build();

      Optional<Income> actualIncomeOptional = incomeRepository.findByIdAndUser(1L, user);
      assertThat(actualIncomeOptional).isNotNull().isEmpty();
    }

  }

  @Nested
  @DisplayName("GetTotalValueByMonthAndYearAndUser method")
  class GetTotalValueByMonthAndYearAndUserMethod {

    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    @Sql(scripts = INSERT_MULTIPLE_INCOMES_FOR_ONE_USER)
    @Test
    @DisplayName("getTotalValueByMonthAndYearAndUser must return Optional<BigDecimal> when found for given year, month and user")
    void getTotalValueByMonthAndYearAndUser_MustReturnOptionalBigDecimal_WhenFoundForGivenYearAndMonthAndUser() {
      User user = User.builder().id(1L).build();

      Optional<BigDecimal> actualTotalValueByMonthAndYearAndUser = incomeRepository
          .getTotalValueByMonthAndYearAndUser(2023, 2, user);

      assertThat(actualTotalValueByMonthAndYearAndUser).isNotNull().isNotEmpty().contains(new BigDecimal("4025.00"));
    }

    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    @Sql(scripts = INSERT_MULTIPLE_INCOMES_AND_MULTIPLE_USERS)
    @Test
    @DisplayName("getTotalValueByMonthAndYearAndUser must return total value only for given user when exists incomes with same year and month and different users")
    void getTotalValueByMonthAndYearAndUser_MustTotalValueOnlyForGivenUser_WhenExistsIncomesWithSameYearAndMonthAndDifferentUsers() {
      User user = User.builder().id(1L).build();

      Optional<BigDecimal> actualTotalValueByMonthAndYearAndUser = incomeRepository
          .getTotalValueByMonthAndYearAndUser(2023, 2, user);

      assertThat(actualTotalValueByMonthAndYearAndUser).isNotNull().isNotEmpty().contains(new BigDecimal("3825.00"));
    }

    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    @Sql(scripts = INSERT_MULTIPLE_INCOMES_FOR_ONE_USER)
    @Test
    @DisplayName("getTotalValueByMonthAndYearAndUser must return empty Optional when not found for given user")
    void getTotalValueByMonthAndYearAndUser_MustReturnEmptyOptional_WhenNotFoundForGivenUser() {
      User user = User.builder().id(2L).build();

      Optional<BigDecimal> actualTotalValueByMonthAndYearAndUser = incomeRepository
          .getTotalValueByMonthAndYearAndUser(2023, 2, user);

      assertThat(actualTotalValueByMonthAndYearAndUser).isNotNull().isEmpty();
    }

    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    @Sql(scripts = INSERT_MULTIPLE_INCOMES_FOR_ONE_USER)
    @Test
    @DisplayName("getTotalValueByMonthAndYearAndUser must return empty Optional when not found for given year")
    void getTotalValueByMonthAndYearAndUser_MustReturnEmptyOptional_WhenNotFoundForGivenYear() {
      User user = User.builder().id(1L).build();

      Optional<BigDecimal> actualTotalValueByMonthAndYearAndUser = incomeRepository
          .getTotalValueByMonthAndYearAndUser(2021, 2, user);

      assertThat(actualTotalValueByMonthAndYearAndUser).isNotNull().isEmpty();
    }

    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    @Sql(scripts = INSERT_MULTIPLE_INCOMES_FOR_ONE_USER)
    @Test
    @DisplayName("getTotalValueByMonthAndYearAndUser must return empty Optional when not found for given month")
    void getTotalValueByMonthAndYearAndUser_MustReturnEmptyOptional_WhenNotFoundForGivenMonth() {
      User user = User.builder().id(1L).build();

      Optional<BigDecimal> actualTotalValueByMonthAndYearAndUser = incomeRepository
          .getTotalValueByMonthAndYearAndUser(2023, 11, user);

      assertThat(actualTotalValueByMonthAndYearAndUser).isNotNull().isEmpty();
    }

  }

}
