package br.com.emendes.financesapi.unit.repository;

import java.time.LocalDate;
import java.util.Optional;

import br.com.emendes.financesapi.repository.ExpenseRepository;
import br.com.emendes.financesapi.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;

import br.com.emendes.financesapi.creator.ExpenseCreator;
import br.com.emendes.financesapi.creator.UserCreator;
import br.com.emendes.financesapi.model.Expense;
import br.com.emendes.financesapi.model.User;

@DataJpaTest
@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("Tests for ExpenseRepository")
public class ExpenseRepositoryTests {

  @Autowired
  private ExpenseRepository expenseRepository;
  @Autowired
  private UserRepository userRepository;

  private User user;

  @BeforeAll
  public void saveUser() {
    this.user = this.userRepository.save(UserCreator.validUserForExpenseRepositoryTest());
  }

  @Test
  @DisplayName("Save must persist Expense when successful")
  void save_PersistExpense_WhenSuccessful() {
    Expense expenseToBeSaved = ExpenseCreator.validExpenseWithoutId(user);

    Expense expenseSaved = this.expenseRepository.save(expenseToBeSaved);

    Assertions.assertThat(expenseSaved).isNotNull();
    Assertions.assertThat(expenseSaved.getId()).isNotNull();
    Assertions.assertThat(expenseSaved.getDescription()).isEqualTo(expenseSaved.getDescription());
  }

  @Test
  @DisplayName("Save must update Expense when successful")
  void save_UpdatesExpense_WhenSuccessful() {
    Expense expenseToBeSaved = ExpenseCreator.validExpenseWithoutId(user);
    Expense expenseSaved = this.expenseRepository.save(expenseToBeSaved);

    expenseSaved.setDescription("Combustível");
    expenseSaved.setDate(LocalDate.parse("2022-01-07"));

    Expense expenseUpdated = this.expenseRepository.save(expenseSaved);

    Assertions.assertThat(expenseUpdated)
        .isNotNull()
        .isEqualTo(expenseSaved);
    Assertions.assertThat(expenseUpdated.getDescription()).isEqualTo(expenseSaved.getDescription());
    Assertions.assertThat(expenseUpdated.getDate()).isEqualTo(expenseSaved.getDate());
  }

  @Test
  @DisplayName("DeleteById must remove Expense when successful")
  void deleteById_RemovesExpense_WhenSuccessful() {
    Expense expenseToBeSaved = ExpenseCreator.validExpenseWithoutId(user);

    Expense expenseSaved = this.expenseRepository.save(expenseToBeSaved);
    Long id = expenseSaved.getId();

    this.expenseRepository.deleteById(id);

    Optional<Expense> ExpenseOptional = this.expenseRepository.findById(id);

    Assertions.assertThat(ExpenseOptional.isEmpty()).isTrue();
  }

  @Test
  @DisplayName("findByUserId must returns page of Expense when successful")
  void findByUserId_ReturnsPageOfExpense_WhenSuccessful() {
    Expense expenseToBeSaved = ExpenseCreator.validExpense();
    expenseToBeSaved.setUser(this.user);
    Expense expenseSaved = this.expenseRepository.save(expenseToBeSaved);

    Long userId = expenseSaved.getUser().getId();
    Pageable pageable = PageRequest.of(0, 10, Direction.DESC, "date");

    Page<Expense> expensePage = this.expenseRepository.findByUserId(userId,
        pageable);

    Assertions.assertThat(expensePage.getNumberOfElements()).isEqualTo(1);
    Assertions.assertThat(expensePage.getContent().get(0).getDescription()).isEqualTo(expenseSaved.getDescription());
  }

  @Test
  @DisplayName("findByUserId must returns empty page of Expense when userId don't exists")
  void findByUserId_ReturnsEmptyPageOfExpense_WhenUserIdDontExists() {
    Long userId = 999l;
    Pageable pageable = PageRequest.of(0, 10, Direction.DESC, "date");

    Page<Expense> expensePage = this.expenseRepository.findByUserId(userId,
        pageable);

    Assertions.assertThat(expensePage).isEmpty();
  }

  @Test
  @DisplayName("findByIdAndUserId must return empty optional when id don't exists")
  void findByIdAndUserId_ReturnsEmptyOptionalExpense_WhenIdDontExists() {
    Long id = 1000l;
    Long userId = this.user.getId();

    Optional<Expense> expenseOptional = this.expenseRepository.findByIdAndUserId(id, userId);

    Assertions.assertThat(expenseOptional).isEmpty();
  }

  @Test
  @DisplayName("findByDescriptionAndUserId must returns page of Expense when successful")
  void findByDescriptionAndUserId_ReturnsPageOfExpense_WhenSuccessful() {
    Expense expenseToBeSaved = ExpenseCreator.validExpenseWithUser(this.user);
    this.expenseRepository.save(expenseToBeSaved);

    String description = "lina";
    Pageable pageable = PageRequest.of(0, 10, Direction.DESC, "date");
    Long userId = this.user.getId();

    Page<Expense> pageExpense = this.expenseRepository.findByDescriptionAndUserId(description, userId, pageable);

    Assertions.assertThat(pageExpense).isNotEmpty();
    Assertions.assertThat(pageExpense.getContent()).hasSize(1);
  }

  @Test
  @DisplayName("findByDescriptionAndUserId must returns empty page of Expense when description don't match")
  void findByDescriptionAndUserId_ReturnsEmptyPageOfExpense_WhenDescriptionDontMatch() {
    String description = "aaaaaaaaaaa";
    Pageable pageable = PageRequest.of(0, 10, Direction.DESC, "date");
    Long userId = this.user.getId();

    Page<Expense> pageExpense = this.expenseRepository.findByDescriptionAndUserId(description, userId, pageable);

    Assertions.assertThat(pageExpense).isEmpty();
  }

  @Test
  @DisplayName("findByDescriptionAndUserId must returns empty page of Expense when userId don't exists")
  void findByDescriptionAndUserId_ReturnsEmptyPageOfExpense_WhenUserIdDontExists() {
    Expense expenseToBeSaved = ExpenseCreator.validExpenseWithoutId(user);
    this.expenseRepository.save(expenseToBeSaved);

    String description = "lina";
    Pageable pageable = PageRequest.of(0, 10, Direction.DESC, "date");
    Long userId = 999l;

    Page<Expense> pageExpense = this.expenseRepository.findByDescriptionAndUserId(description, userId, pageable);

    Assertions.assertThat(pageExpense).isEmpty();
  }

  @Test
  @DisplayName("findByYearAndMonthAndUserId must returns page of Expense when successful")
  void findByYearAndMonthAndUserId_ReturnsPageOfExpense_WhenSuccessful() {
    Expense expenseToBeSaved = ExpenseCreator.validExpenseWithUser(this.user);
    Expense expenseSaved = this.expenseRepository.save(expenseToBeSaved);

    Integer year = expenseSaved.getDate().getYear();
    Integer month = expenseSaved.getDate().getMonthValue();
    Pageable pageable = PageRequest.of(0, 10, Direction.DESC, "date");
    Long userId = this.user.getId();

    Page<Expense> pageExpense = this.expenseRepository.findByYearAndMonthAndUserId(year, month, userId, pageable);

    Assertions.assertThat(pageExpense).isNotEmpty();
    Assertions.assertThat(pageExpense.getContent()).hasSize(1);
  }

  @Test
  @DisplayName("findByYearAndMonthAndUserId must returns empty page of Expense when userId don't exists")
  void findByYearAndMonthAndUserId_ReturnsEmptyPageOfExpense_WhenUserIdDontExists() {
    Expense expenseToBeSaved = ExpenseCreator.validExpenseWithoutId(user);
    Expense expenseSaved = this.expenseRepository.save(expenseToBeSaved);

    Integer year = expenseSaved.getDate().getYear();
    Integer month = expenseSaved.getDate().getMonthValue();
    Pageable pageable = PageRequest.of(0, 10, Direction.DESC, "date");
    Long userId = 999l;

    Page<Expense> pageExpense = this.expenseRepository.findByYearAndMonthAndUserId(year, month, userId, pageable);

    Assertions.assertThat(pageExpense).isEmpty();
  }

  @Test
  @DisplayName("findByYearAndMonthAndUserId must returns empty page of Expense when year don't exists")
  void findByYearAndMonthAndUserId_ReturnsEmptyPageOfExpense_WhenYearDontExists() {
    Expense expenseToBeSaved = ExpenseCreator.validExpenseWithoutId(user);
    Expense expenseSaved = this.expenseRepository.save(expenseToBeSaved);

    Integer year = 3000;
    Integer month = expenseSaved.getDate().getMonthValue();
    Pageable pageable = PageRequest.of(0, 10, Direction.DESC, "date");
    Long userId = this.user.getId();

    Page<Expense> pageExpense = this.expenseRepository.findByYearAndMonthAndUserId(year, month, userId, pageable);

    Assertions.assertThat(pageExpense).isEmpty();
  }

  @Test
  @DisplayName("findByYearAndMonthAndUserId must returns empty page of Expense when month don't exists")
  void findByYearAndMonthAndUserId_ReturnsEmptyPageOfExpense_WhenMonthDontExists() {
    Expense expenseToBeSaved = ExpenseCreator.validExpenseWithoutId(user);
    Expense expenseSaved = this.expenseRepository.save(expenseToBeSaved);

    Integer year = expenseSaved.getDate().getYear();
    Integer month = 12;
    Pageable pageable = PageRequest.of(0, 10, Direction.DESC, "date");
    Long userId = this.user.getId();

    Page<Expense> pageExpense = this.expenseRepository.findByYearAndMonthAndUserId(year, month, userId, pageable);

    Assertions.assertThat(pageExpense).isEmpty();
  }
}
