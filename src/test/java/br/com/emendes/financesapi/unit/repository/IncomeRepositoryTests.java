//package br.com.emendes.financesapi.unit.repository;
//
//import java.time.LocalDate;
//import java.util.Optional;
//
//import br.com.emendes.financesapi.repository.IncomeRepository;
//import br.com.emendes.financesapi.repository.UserRepository;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestInstance;
//import org.junit.jupiter.api.TestInstance.Lifecycle;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort.Direction;
//
//import br.com.emendes.financesapi.util.creator.IncomeCreator;
//import br.com.emendes.financesapi.util.creator.UserCreator;
//import br.com.emendes.financesapi.model.Income;
//import br.com.emendes.financesapi.model.User;
//
//@DataJpaTest
//@TestInstance(Lifecycle.PER_CLASS)
//@DisplayName("Tests for IncomeRepository")
//public class IncomeRepositoryTests {
//
//  @Autowired
//  private IncomeRepository incomeRepository;
//  @Autowired
//  private UserRepository userRepository;
//
//  private User user;
//
//  @BeforeAll
//  public void saveUser() {
//    this.user = this.userRepository.save(UserCreator.validUserForIncomeRepositoryTest());
//  }
//
//  @Test
//  @DisplayName("Save must persist Income when successful")
//  void save_PersistIncome_WhenSuccessful() {
//    Income incomeToBeSaved = IncomeCreator.validIncomeWithoutId();
//
//    Income incomeSaved = this.incomeRepository.save(incomeToBeSaved);
//
//    Assertions.assertThat(incomeSaved).isNotNull();
//    Assertions.assertThat(incomeSaved.getId()).isNotNull();
//    Assertions.assertThat(incomeSaved.getDescription()).isEqualTo(incomeToBeSaved.getDescription());
//  }
//
//  @Test
//  @DisplayName("Save must update Income when successful")
//  void save_UpdatesIncome_WhenSuccessful() {
//    Income incomeToBeSaved = IncomeCreator.validIncomeWithoutId();
//    Income incomeSaved = this.incomeRepository.save(incomeToBeSaved);
//
//    incomeSaved.setDescription("Sálario 1");
//    incomeSaved.setDate(LocalDate.parse("2022-01-07"));
//
//    Income incomeUpdated = this.incomeRepository.save(incomeSaved);
//
//    Assertions.assertThat(incomeUpdated)
//        .isNotNull()
//        .isEqualTo(incomeSaved);
//    Assertions.assertThat(incomeUpdated.getDescription()).isEqualTo(incomeSaved.getDescription());
//    Assertions.assertThat(incomeUpdated.getDate()).isEqualTo(incomeSaved.getDate());
//  }
//
//  @Test
//  @DisplayName("DeleteById must remove Income when successful")
//  void deleteById_RemovesIncome_WhenSuccessful() {
//    Income incomeToBeSaved = IncomeCreator.validIncomeWithoutId();
//
//    Income incomeSaved = this.incomeRepository.save(incomeToBeSaved);
//    Long id = incomeSaved.getId();
//
//    this.incomeRepository.deleteById(id);
//
//    Optional<Income> incomeOptional = this.incomeRepository.findById(id);
//
//    Assertions.assertThat(incomeOptional.isEmpty()).isTrue();
//  }
//
//  @Test
//  @DisplayName("findByUserId must returns page of Income when successful")
//  void findByUserId_ReturnsPageOfIncome_WhenSuccessful() {
//    Income incomeToBeSaved = IncomeCreator.validIncomeWithUser(this.user);
//    Income incomeSaved = this.incomeRepository.save(incomeToBeSaved);
//
//    Long userId = incomeSaved.getUser().getId();
//    Pageable pageable = PageRequest.of(0, 10, Direction.DESC, "date");
//
//    Page<Income> incomePage = this.incomeRepository.findByUserId(userId,
//        pageable);
//
//    Assertions.assertThat(incomePage.getNumberOfElements()).isEqualTo(1);
//    Assertions.assertThat(incomePage.getContent().get(0).getDescription()).isEqualTo(incomeSaved.getDescription());
//  }
//
//  @Test
//  @DisplayName("findByUserId must returns empty page of Income when userId don't exists")
//  void findByUserId_ReturnsEmptyPageOfIncome_WhenUserIdDontExists() {
//    Long userId = 999l;
//    Pageable pageable = PageRequest.of(0, 10, Direction.DESC, "date");
//
//    Page<Income> incomePage = this.incomeRepository.findByUserId(userId,
//        pageable);
//
//    Assertions.assertThat(incomePage).isEmpty();
//  }
//
//  @Test
//  @DisplayName("findByIdAndUserId must return empty optional when id don't exists")
//  void findByIdAndUserId_ReturnsEmptyOptionalIncome_WhenIdDontExists() {
//    Long id = 1000l;
//    Long userId = this.user.getId();
//
//    Optional<Income> incomeOptional = this.incomeRepository.findByIdAndUserId(id, userId);
//
//    Assertions.assertThat(incomeOptional).isEmpty();
//  }
//
//  @Test
//  @DisplayName("findByDescriptionAndUserId must returns page of Income when successful")
//  void findByDescriptionAndUserId_ReturnsPageOfIncome_WhenSuccessful() {
//    Income incomeToBeSaved = IncomeCreator.validIncomeWithUser(this.user);
//    this.incomeRepository.save(incomeToBeSaved);
//
//    String description = "ario";
//    Pageable pageable = PageRequest.of(0, 10, Direction.DESC, "date");
//    Long userId = this.user.getId();
//
//    Page<Income> pageIncome = this.incomeRepository.findByDescriptionAndUserId(description, userId, pageable);
//
//    Assertions.assertThat(pageIncome).isNotEmpty();
//    Assertions.assertThat(pageIncome.getContent()).hasSize(1);
//  }
//
//  @Test
//  @DisplayName("findByDescriptionAndUserId must returns empty page of Income when description don't match")
//  void findByDescriptionAndUserId_ReturnsEmptyPageOfIncome_WhenDescriptionDontMatch() {
//    String description = "aaaaaaaaaaa";
//    Pageable pageable = PageRequest.of(0, 10, Direction.DESC, "date");
//    Long userId = this.user.getId();
//
//    Page<Income> pageIncome = this.incomeRepository.findByDescriptionAndUserId(description, userId, pageable);
//
//    Assertions.assertThat(pageIncome).isEmpty();
//  }
//
//  @Test
//  @DisplayName("findByDescriptionAndUserId must returns empty page of Income when userId don't exists")
//  void findByDescriptionAndUserId_ReturnsEmptyPageOfIncome_WhenUserIdDontExists() {
//    Income incomeToBeSaved = IncomeCreator.validIncomeWithoutId();
//    this.incomeRepository.save(incomeToBeSaved);
//
//    String description = "ario";
//    Pageable pageable = PageRequest.of(0, 10, Direction.DESC, "date");
//    Long userId = 999l;
//
//    Page<Income> pageIncome = this.incomeRepository.findByDescriptionAndUserId(description, userId, pageable);
//
//    Assertions.assertThat(pageIncome).isEmpty();
//  }
//
//  @Test
//  @DisplayName("findByYearAndMonthAndUserId must returns page of Income when successful")
//  void findByYearAndMonthAndUserId_ReturnsPageOfIncome_WhenSuccessful() {
//    Income incomeToBeSaved = IncomeCreator.validIncomeWithUser(this.user);
//    Income incomeSaved = this.incomeRepository.save(incomeToBeSaved);
//
//    Integer year = incomeSaved.getDate().getYear();
//    Integer month = incomeSaved.getDate().getMonthValue();
//    Pageable pageable = PageRequest.of(0, 10, Direction.DESC, "date");
//    Long userId = this.user.getId();
//
//    Page<Income> pageIncome = this.incomeRepository.findByYearAndMonthAndUserId(year, month, userId, pageable);
//
//    Assertions.assertThat(pageIncome).isNotEmpty();
//    Assertions.assertThat(pageIncome.getContent()).hasSize(1);
//  }
//
//  @Test
//  @DisplayName("findByYearAndMonthAndUserId must returns empty page of Income when userId don't exists")
//  void findByYearAndMonthAndUserId_ReturnsEmptyPageOfIncome_WhenUserIdDontExists() {
//    Income incomeToBeSaved = IncomeCreator.validIncomeWithoutId();
//    Income incomeSaved = this.incomeRepository.save(incomeToBeSaved);
//
//    Integer year = incomeSaved.getDate().getYear();
//    Integer month = incomeSaved.getDate().getMonthValue();
//    Pageable pageable = PageRequest.of(0, 10, Direction.DESC, "date");
//    Long userId = 999l;
//
//    Page<Income> pageIncome = this.incomeRepository.findByYearAndMonthAndUserId(year, month, userId, pageable);
//
//    Assertions.assertThat(pageIncome).isEmpty();
//  }
//
//  @Test
//  @DisplayName("findByYearAndMonthAndUserId must returns empty page of Income when year don't exists")
//  void findByYearAndMonthAndUserId_ReturnsEmptyPageOfIncome_WhenYearDontExists() {
//    Income incomeToBeSaved = IncomeCreator.validIncomeWithUser(this.user);
//    Income incomeSaved = this.incomeRepository.save(incomeToBeSaved);
//
//    Integer year = 3000;
//    Integer month = incomeSaved.getDate().getMonthValue();
//    Pageable pageable = PageRequest.of(0, 10, Direction.DESC, "date");
//    Long userId = this.user.getId();
//
//    Page<Income> pageIncome = this.incomeRepository.findByYearAndMonthAndUserId(year, month, userId, pageable);
//
//    Assertions.assertThat(pageIncome).isEmpty();
//  }
//
//  @Test
//  @DisplayName("findByYearAndMonthAndUserId must returns empty page of Income when month don't exists")
//  void findByYearAndMonthAndUserId_ReturnsEmptyPageOfIncome_WhenMonthDontExists() {
//    Income incomeToBeSaved = IncomeCreator.validIncomeWithUser(this.user);
//    Income incomeSaved = this.incomeRepository.save(incomeToBeSaved);
//
//    Integer year = incomeSaved.getDate().getYear();
//    Integer month = 12;
//    Pageable pageable = PageRequest.of(0, 10, Direction.DESC, "date");
//    Long userId = this.user.getId();
//
//    Page<Income> pageIncome = this.incomeRepository.findByYearAndMonthAndUserId(year, month, userId, pageable);
//
//    Assertions.assertThat(pageIncome).isEmpty();
//  }
//
//}
