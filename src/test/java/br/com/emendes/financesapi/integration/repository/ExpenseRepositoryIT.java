package br.com.emendes.financesapi.integration.repository;

import br.com.emendes.financesapi.dto.response.ValueByCategoryResponse;
import br.com.emendes.financesapi.model.Category;
import br.com.emendes.financesapi.model.entity.Expense;
import br.com.emendes.financesapi.model.entity.User;
import br.com.emendes.financesapi.repository.ExpenseRepository;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static br.com.emendes.financesapi.util.constant.ConstantForTesting.PAGEABLE;
import static br.com.emendes.financesapi.util.constant.SqlPath.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests da camada expense repository interagindo com o banco de dados.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("repository-it")
@DisplayName("Integration tests for ExpenseRepository")
@SqlGroup({
    @Sql(scripts = {DROP_DATABASE_TABLES_SQL_PATH, CREATE_DATABASE_TABLES_SQL_PATH})
})
class ExpenseRepositoryIT {

  @Autowired
  private ExpenseRepository expenseRepository;

  @Nested
  @DisplayName("FindAllByUser method")
  class FindAllByUserMethod {

    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    @Sql(scripts = INSERT_MULTIPLE_EXPENSES_AND_MULTIPLE_USERS)
    @Test
    @DisplayName("findAllByUser must return page with two expenses when found expenses for given User")
    void findAllByUser_MustReturnPageWithTwoExpenses_WhenFoundExpensesForGivenUser() {
      User user = User.builder().id(1L).build();

      Page<Expense> actualExpensePage = expenseRepository.findAllByUser(user, PAGEABLE);

      assertThat(actualExpensePage).isNotNull().hasSize(2);
    }

    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    @Sql(scripts = INSERT_USER_SQL_PATH)
    @Test
    @DisplayName("findAllByUser must return empty page when not found expenses for given User")
    void findAllByUser_MustReturnEmptyPage_WhenNotFoundExpensesForGivenUser() {
      User user = User.builder().id(1L).build();

      Page<Expense> actualExpensePage = expenseRepository.findAllByUser(user, PAGEABLE);

      assertThat(actualExpensePage).isNotNull().isEmpty();
    }

  }

  @Nested
  @DisplayName("FindByDescriptionAndUser method")
  class FindByDescriptionAndUserMethod {

    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    @Sql(scripts = INSERT_MULTIPLE_EXPENSES_FOR_ONE_USER)
    @Test
    @DisplayName("findByDescriptionAndUser must return page with three expenses when found for given user and description 'merc'")
    void findByDescriptionAndUser_MustReturnPageWithThreeExpenses_WhenFoundForGivenUserAndDescriptionMerc() {
      User user = User.builder().id(1L).build();

      Page<Expense> actualExpensePage = expenseRepository.findByDescriptionAndUser("merc", user, PAGEABLE);
      assertThat(actualExpensePage).isNotNull().hasSize(3);

      Page<String> actualExpensesDescriptions = actualExpensePage.map(Expense::getDescription);
      assertThat(actualExpensesDescriptions)
          .isNotNull().hasSize(3)
          .allMatch(description -> description.toLowerCase().contains("merc"));
    }

    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    @Sql(scripts = INSERT_MULTIPLE_EXPENSES_AND_MULTIPLE_USERS)
    @Test
    @DisplayName("findByDescriptionAndUser must return page with one expense when exists expense with same description for different users")
    void findByDescriptionAndUser_MustReturnPageWithOneExpense_WhenExistsExpenseWithSameDescriptionForDifferentUsers() {
      User user = User.builder().id(1L).build();

      Page<Expense> actualExpensePage = expenseRepository.findByDescriptionAndUser("aluguel", user, PAGEABLE);
      assertThat(actualExpensePage).isNotNull().hasSize(1);

      Page<String> actualExpensesDescriptions = actualExpensePage.map(Expense::getDescription);
      assertThat(actualExpensesDescriptions)
          .isNotNull().hasSize(1)
          .allMatch(description -> description.toLowerCase().contains("aluguel"));
    }

    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    @Sql(scripts = INSERT_MULTIPLE_EXPENSES_FOR_ONE_USER)
    @Test
    @DisplayName("findByDescriptionAndUser must return empty page when not found for given user and description 'mecanico'")
    void findByDescriptionAndUser_MustReturnEmptyPage_WhenNotFoundForGivenUserAndDescriptionMecanico() {
      User user = User.builder().id(1L).build();

      Page<Expense> actualExpensePage = expenseRepository.findByDescriptionAndUser("mecanico", user, PAGEABLE);
      assertThat(actualExpensePage).isNotNull().isEmpty();
    }


    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    @Sql(scripts = INSERT_MULTIPLE_EXPENSES_AND_MULTIPLE_USERS)
    @Test
    @DisplayName("findByDescriptionAndUser must return empty page when not found for given user")
    void findByDescriptionAndUser_MustReturnEmptyPage_WhenNotFoundForGivenUser() {
      User user = User.builder().id(1L).build();

      Page<Expense> actualExpensePage = expenseRepository.findByDescriptionAndUser("super", user, PAGEABLE);
      assertThat(actualExpensePage).isNotNull().isEmpty();
    }

  }

  @Nested
  @DisplayName("FindByYearAndMonthAndUser method")
  class FindByYearAndMonthAndUserMethod {

    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    @Sql(scripts = INSERT_MULTIPLE_EXPENSES_FOR_ONE_USER)
    @Test
    @DisplayName("findByYearAndMonthAndUser must return Page with three expenses when found for given user, year and month")
    void findByYearAndMonthAndUser_MustReturnPageWithThreeExpenses_WhenFoundForGivenUserAndYearAndMonth() {
      User user = User.builder().id(1L).build();

      Page<Expense> actualExpensePage = expenseRepository.findByYearAndMonthAndUser(2023, 2, user, PAGEABLE);
      assertThat(actualExpensePage).isNotNull().hasSize(3);

      Page<LocalDate> actualExpensesDescriptions = actualExpensePage.map(Expense::getDate);
      assertThat(actualExpensesDescriptions)
          .isNotNull().hasSize(3)
          .allMatch(date -> date.getYear() == 2023 && date.getMonthValue() == 2);
    }

    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    @Sql(scripts = INSERT_MULTIPLE_EXPENSES_AND_MULTIPLE_USERS)
    @Test
    @DisplayName("findByYearAndMonthAndUser must return Page with two expenses when exists expenses with same year and month and different users")
    void findByYearAndMonthAndUser_MustReturnPageWithTwoExpenses_WhenExistsExpenseWithSameYearAndMonthAndDifferentUsers() {
      User user = User.builder().id(1L).build();

      Page<Expense> actualExpensePage = expenseRepository.findByYearAndMonthAndUser(2023, 2, user, PAGEABLE);
      assertThat(actualExpensePage).isNotNull().hasSize(2);

      Page<LocalDate> actualExpensesDescriptions = actualExpensePage.map(Expense::getDate);
      assertThat(actualExpensesDescriptions)
          .isNotNull().hasSize(2)
          .allMatch(date -> date.getYear() == 2023 && date.getMonthValue() == 2);
    }

    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    @Sql(scripts = INSERT_MULTIPLE_EXPENSES_FOR_ONE_USER)
    @Test
    @DisplayName("findByYearAndMonthAndUser must return empty Page when not found for given user, year and month")
    void findByYearAndMonthAndUser_MustReturnEmptyPage_WhenNotFoundForGivenUserAndYearAndMonth() {
      User user = User.builder().id(2L).build();

      Page<Expense> actualExpensePage = expenseRepository.findByYearAndMonthAndUser(2021, 11, user, PAGEABLE);
      assertThat(actualExpensePage).isNotNull().isEmpty();
    }

    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    @Sql(scripts = INSERT_MULTIPLE_EXPENSES_FOR_ONE_USER)
    @Test
    @DisplayName("findByYearAndMonthAndUser must return empty Page when not found for given user")
    void findByYearAndMonthAndUser_MustReturnEmptyPage_WhenNotFoundForGivenUser() {
      User user = User.builder().id(2L).build();

      Page<Expense> actualExpensePage = expenseRepository.findByYearAndMonthAndUser(2023, 3, user, PAGEABLE);
      assertThat(actualExpensePage).isNotNull().isEmpty();
    }

    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    @Sql(scripts = INSERT_MULTIPLE_EXPENSES_FOR_ONE_USER)
    @Test
    @DisplayName("findByYearAndMonthAndUser must return empty Page when not found for given year")
    void findByYearAndMonthAndUser_MustReturnEmptyPage_WhenNotFoundForGivenYear() {
      User user = User.builder().id(1L).build();

      Page<Expense> actualExpensePage = expenseRepository.findByYearAndMonthAndUser(2021, 3, user, PAGEABLE);
      assertThat(actualExpensePage).isNotNull().isEmpty();
    }

    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    @Sql(scripts = INSERT_MULTIPLE_EXPENSES_FOR_ONE_USER)
    @Test
    @DisplayName("findByYearAndMonthAndUser must return empty Page when not found for given month")
    void findByYearAndMonthAndUser_MustReturnEmptyPage_WhenNotFoundForGivenMonth() {
      User user = User.builder().id(1L).build();

      Page<Expense> actualExpensePage = expenseRepository.findByYearAndMonthAndUser(2023, 11, user, PAGEABLE);
      assertThat(actualExpensePage).isNotNull().isEmpty();
    }

  }

  @Nested
  @DisplayName("FindByIdAndUser method")
  class FindByIdAndUserMethod {

    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    @Sql(scripts = INSERT_MULTIPLE_EXPENSES_FOR_ONE_USER)
    @Test
    @DisplayName("findByIdAndUser must return Optional<Expense> when found for given id and user")
    void findByIdAndUser_MustReturnPageWithThreeExpenses_WhenFoundForGivenIdAndUser() {
      User user = User.builder().id(1L).build();

      Optional<Expense> actualExpenseOptional = expenseRepository.findByIdAndUser(2L, user);
      assertThat(actualExpenseOptional).isNotNull().isNotEmpty();
    }

    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    @Sql(scripts = INSERT_MULTIPLE_EXPENSES_FOR_ONE_USER)
    @Test
    @DisplayName("findByIdAndUser must return empty Optional when not found for given id and user")
    void findByIdAndUser_MustReturnEmptyOptional_WhenNotFoundForGivenIdAndUser() {
      User user = User.builder().id(2L).build();

      Optional<Expense> actualExpenseOptional = expenseRepository.findByIdAndUser(100L, user);
      assertThat(actualExpenseOptional).isNotNull().isEmpty();
    }

    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    @Sql(scripts = INSERT_MULTIPLE_EXPENSES_FOR_ONE_USER)
    @Test
    @DisplayName("findByIdAndUser must return empty Optional when not found for given id")
    void findByIdAndUser_MustReturnEmptyOptional_WhenNotFoundForGivenId() {
      User user = User.builder().id(1L).build();

      Optional<Expense> actualExpenseOptional = expenseRepository.findByIdAndUser(100L, user);
      assertThat(actualExpenseOptional).isNotNull().isEmpty();
    }

    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    @Sql(scripts = INSERT_MULTIPLE_EXPENSES_FOR_ONE_USER)
    @Test
    @DisplayName("findByIdAndUser must return empty Optional when not found for given user")
    void findByIdAndUser_MustReturnEmptyOptional_WhenNotFoundForGivenUser() {
      User user = User.builder().id(2L).build();

      Optional<Expense> actualExpenseOptional = expenseRepository.findByIdAndUser(1L, user);
      assertThat(actualExpenseOptional).isNotNull().isEmpty();
    }

  }

  @Nested
  @DisplayName("GetValueByCategoryAndMonthAndYearAndUser method")
  class GetValueByCategoryAndMonthAndYearAndUserMethod {

    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    @Sql(scripts = INSERT_MULTIPLE_EXPENSES_FOR_ONE_USER)
    @Test
    @DisplayName("getValueByCategoryAndMonthAndYearAndUser must return List<ValueByCategoryResponse> when found for given year, month and user")
    void getValueByCategoryAndMonthAndYearAndUser_MustReturnListValueByCategoryResponse_WhenFoundForGivenYearAndMonthAndUser() {
      User user = User.builder().id(1L).build();

      List<ValueByCategoryResponse> actualValueByCategoryList = expenseRepository
          .getValueByCategoryAndMonthAndYearAndUser(2023, 2, user);

      assertThat(actualValueByCategoryList).isNotNull().hasSize(2)
          .allMatch(valueByCategoryResponse ->
              valueByCategoryResponse.getCategory().equals(Category.MORADIA) ||
              valueByCategoryResponse.getCategory().equals(Category.ALIMENTACAO));
    }

    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    @Sql(scripts = INSERT_MULTIPLE_EXPENSES_FOR_ONE_USER)
    @Test
    @DisplayName("getValueByCategoryAndMonthAndYearAndUser must return empty List when not found for given user")
    void getValueByCategoryAndMonthAndYearAndUser_MustReturnEmptyList_WhenNotFoundForGivenUser() {
      User user = User.builder().id(2L).build();

      List<ValueByCategoryResponse> actualValueByCategoryList = expenseRepository
          .getValueByCategoryAndMonthAndYearAndUser(2023, 2, user);

      assertThat(actualValueByCategoryList).isNotNull().isEmpty();
    }

    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    @Sql(scripts = INSERT_MULTIPLE_EXPENSES_FOR_ONE_USER)
    @Test
    @DisplayName("getValueByCategoryAndMonthAndYearAndUser must return empty List when not found for given year")
    void getValueByCategoryAndMonthAndYearAndUser_MustReturnEmptyList_WhenNotFoundForGivenYear() {
      User user = User.builder().id(1L).build();

      List<ValueByCategoryResponse> actualValueByCategoryList = expenseRepository
          .getValueByCategoryAndMonthAndYearAndUser(2021, 2, user);

      assertThat(actualValueByCategoryList).isNotNull().isEmpty();
    }

    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    @Sql(scripts = INSERT_MULTIPLE_EXPENSES_FOR_ONE_USER)
    @Test
    @DisplayName("getValueByCategoryAndMonthAndYearAndUser must return empty List when not found for given month")
    void getValueByCategoryAndMonthAndYearAndUser_MustReturnEmptyList_WhenNotFoundForGivenMonth() {
      User user = User.builder().id(1L).build();

      List<ValueByCategoryResponse> actualValueByCategoryList = expenseRepository
          .getValueByCategoryAndMonthAndYearAndUser(2021, 11, user);

      assertThat(actualValueByCategoryList).isNotNull().isEmpty();
    }

  }

}
