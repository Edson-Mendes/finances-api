package br.com.emendes.financesapi.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.emendes.financesapi.model.Expense;
import br.com.emendes.financesapi.model.enumerator.Category;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

  @Query("SELECT e FROM Expense e WHERE e.user.id = ?#{ principal?.id }")
  Page<Expense> findAllByUser(Pageable pageable);

  // FIXME: Refatorar essa busca
  @Query("SELECT e FROM Expense e WHERE e.description = :description AND " +
      "MONTH(e.date) = :month AND YEAR(e.date) = :year AND e.user.id = ?#{ principal?.id }")
  Optional<Expense> findByDescriptionAndMonthAndYearAndUser(
      @Param("description") String description,
      @Param("month") Integer month,
      @Param("year") Integer year);

  // FIXME: Refatorar essa busca
  @Query("SELECT e FROM Expense e WHERE e.description = :description AND " +
      "MONTH(e.date) = :month AND YEAR(e.date) = :year AND e.user.id = ?#{ principal?.id } AND e.id != :id")
  Optional<Expense> findByDescriptionAndMonthAndYearAndNotIdAndUser(
      @Param("description") String description,
      @Param("month") Integer month,
      @Param("year") Integer year,
      @Param("id") Long id);

  @Query("SELECT e FROM Expense e WHERE e.description LIKE %:description% AND e.user.id = ?#{ principal?.id }")
  Page<Expense> findByDescriptionAndUser(@Param("description") String description, Pageable pageable);

  @Query("SELECT e FROM Expense e WHERE YEAR(e.date) = :year AND MONTH(e.date)= :month AND e.user.id = ?#{ principal?.id }")
  Page<Expense> findByYearAndMonthAndUser(
      @Param("year") Integer year,
      @Param("month") Integer month,
      Pageable pageable);

  @Query("SELECT SUM(e.value) FROM Expense e WHERE YEAR(e.date) = :year AND MONTH(e.date) = :month AND e.user.id = ?#{ principal?.id }")
  Optional<BigDecimal> getTotalValueByMonthAndYearAndUser(
      @Param("year") Integer year,
      @Param("month") Integer month);

  @Query("SELECT SUM(value) FROM Expense e WHERE YEAR(e.date) = :year AND " +
      "MONTH (e.date) = :month AND e.category = :category AND e.user.id = ?#{ principal?.id }")
  Optional<BigDecimal> getTotalByCategoryOnYearAndMonth(
      @Param("category") Category category,
      @Param("year") Integer year,
      @Param("month") Integer month);

  @Query("SELECT e FROM Expense e WHERE e.id = :id AND e.user.id = ?#{ principal?.id }")
  Optional<Expense> findByIdAndUser(@Param("id") Long id);

}
